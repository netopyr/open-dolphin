package org.opendolphin.demo.team;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ReflectionBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import jfxtras.labs.scene.control.gauge.Led;
import jfxtras.labs.scene.control.gauge.LedBuilder;
import org.opendolphin.binding.Converter;
import org.opendolphin.binding.JavaFxUtil;
import org.opendolphin.core.*;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientAttributeWrapper;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.binding.JFXBinder.bind;
import static org.opendolphin.binding.JFXBinder.bindInfo;
import static org.opendolphin.demo.team.TeamMemberConstants.*;

public class TeamApplication extends Application {

    private final Button    ADD_BUTTON           = new Button("+");
    private final Button    SAVE                 = new Button("Save");
    private final Button    RESET                = new Button("Reset");
    private final Button    DELETE               = new Button("Delete");
    private final TextField FIELD_FIRST_NAME     = TextFieldBuilder.create().promptText("e.g. John").build();
    private final TextField FIELD_LAST_NAME      = TextFieldBuilder.create().promptText("e.g. Doe").build();
    private final CheckBox  CHECK_BOX_AVAILABLE  = new CheckBox();
    private final CheckBox  CHECK_BOX_CONTRACTOR = new CheckBox();
    private final Slider    SLIDER_WORKLOAD      = SliderBuilder.create().min(0).max(100).value(0).build();
    private final ComboBox  COMBO_BOX_FUNCTION   = ComboBoxBuilder.create()
        .items(FXCollections.observableArrayList((Object) "", "Engineer", "Architect", "Administrator", "Consultant", "CFO", "CTO", "CEO"))
        .prefWidth(213) // for the moment static. Bind later to col width
        .build();

    private Map<String, Image> lazyImageCache = new HashMap<String, Image>(10);

    private final ImageView IMAGE_FUNCTION = new ImageView(getImage(""));
    private final ImageView IMAGE_ANIM     = new ImageView(getImage(""));

    private TableView<Object> table;

    final ObservableList<PresentationModel> teamMembers = FXCollections.observableArrayList();

    GridPane form;

    static  ClientDolphin           clientDolphin;
    static  ClientDolphin           pollerDolphin;
    private ClientPresentationModel teamMemberMold;
    private ClientPresentationModel blankMold;
    private ClientAttribute         selectedPmId;

    public TeamApplication() {
        teamMemberMold = clientDolphin.presentationModel(PM_ID_MOLD, (String) null,
            new ClientAttribute(ATT_FIRSTNAME, ""),
            new ClientAttribute(ATT_LASTNAME, ""),
            new ClientAttribute(ATT_FUNCTION, ""),
            new ClientAttribute(ATT_AVAILABLE, false),
            new ClientAttribute(ATT_CONTRACTOR, false),
            new ClientAttribute(ATT_WORKLOAD, 0));

        blankMold = clientDolphin.presentationModel(null, (String) null, // use copy when available
            new ClientAttribute(ATT_FIRSTNAME, ""),
            new ClientAttribute(ATT_LASTNAME, ""),
            new ClientAttribute(ATT_FUNCTION, ""),
            new ClientAttribute(ATT_AVAILABLE, false),
            new ClientAttribute(ATT_CONTRACTOR, false),
            new ClientAttribute(ATT_WORKLOAD, 0));

        selectedPmId = new ClientAttribute(ATT_SEL_PM_ID, null, QUAL_SEL_PM_ID, null); /* null for no selection*/
        clientDolphin.presentationModel(PM_ID_SELECTED, (String) null, selectedPmId);
    }

    TableColumn makeTableColumn(String header, final String attributeName) {
        // make sure attribute value changes lead to cell value changes
        final TableColumn column = JavaFxUtil.value(attributeName, new TableColumn(header));
        // display the cell value with the appropriate control and update dirty
        column.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(final TableColumn tableColumn) {
                return new TableCell<ClientAttributeWrapper, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) return;
                        if (item instanceof Boolean) {
                            Node graphic = getGraphic();
                            if (!(graphic instanceof Circle)) {
                                graphic = CircleBuilder.create().radius(10).build();
                                setGraphic(graphic);
                            }
                            final Circle checkBox = (Circle) graphic;
                            checkBox.getStyleClass().clear();
                            checkBox.getStyleClass().add(((Boolean) item) ? "dot-selected" : "dot-unselected");
                            setAlignment(Pos.CENTER);
                        } else if (item instanceof Number) {
                            Node graphic = getGraphic();
                            if (!(graphic instanceof ProgressBar)) {
                                graphic = new ProgressBar();
                                setGraphic(graphic);
                            }
                            final ProgressBar indicator = (ProgressBar) graphic;
                            final Double doubleItem = Double.valueOf(item.toString()); // omg
                            indicator.setProgress(doubleItem / 100);
                            setAlignment(Pos.CENTER);
                        } else {
                            setText(item.toString());
                        }
                        final TableRow tableRow = getTableRow();
                        if (null == tableRow) return;
                        final PresentationModel pm = (PresentationModel) tableRow.getItem();
                        if (null == pm) return;
                        if (pm.getAt(attributeName).isDirty()) {
                            getStyleClass().add("cell-dirty");
                        } else {
                            getStyleClass().removeAll("cell-dirty");
                        }
                    }
                };
            }
        });
        return column;
    }

    @Override
    public void start(final Stage stage) throws Exception {

        final Font font = Font.loadFont(this.getClass().getResourceAsStream("/Eurostile-Demi.ttf"), 18);
        if (null == font) System.out.println("could not load font");

        table = TableViewBuilder.create()
            .columns(
                makeTableColumn("First Name", ATT_FIRSTNAME),
                makeTableColumn("Last Name", ATT_LASTNAME),
                makeTableColumn("Function", ATT_FUNCTION),
                makeTableColumn("Available", ATT_AVAILABLE),
                makeTableColumn("Contractor", ATT_CONTRACTOR),
                makeTableColumn("Workload", ATT_WORKLOAD))
            .items((ObservableList) teamMembers)
            .columnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
            .placeholder(TextBuilder.create().text("Please add a team member").id("no-content").build())
            .build();

        ADD_BUTTON.setTranslateX(5); // quick hack for alignment
        BorderPane root = BorderPaneBuilder.create()
            .center( // table view in the center, the one to grow and shrink
                VBoxBuilder.create().id("master").children(
                    ADD_BUTTON,
                    table
                ).build()
            )
            .right( // detail view to the right
                form = GridPaneBuilder.create()
                    .disable(true)
                    .effect(new BoxBlur())
                    .id("detail")
                    .vgap(5).hgap(10)
                    .columnConstraints(
                        ColumnConstraintsBuilder.create().halignment(HPos.RIGHT).build(),
                        ColumnConstraintsBuilder.create().halignment(HPos.LEFT).build()
                    )
                    .build()
            ).build();

        final Pane imagePane = PaneBuilder.create().children(IMAGE_ANIM, IMAGE_FUNCTION).build();
        imagePane.setEffect(ReflectionBuilder.create().fraction(0.2).topOpacity(0.15).build());
        VBox functionView = VBoxBuilder.create().children(COMBO_BOX_FUNCTION, imagePane).build();

        int row = 0;
        form.add(new Label("First Name"), 0, row);
        form.add(FIELD_FIRST_NAME, 1, row++);
        form.add(new Label("Last  Name"), 0, row);
        form.add(FIELD_LAST_NAME, 1, row++);
        form.add(new Label("Function"), 0, row);
        form.add(functionView, 1, row++);
        form.add(new Label("Available"), 0, row);
        form.add(CHECK_BOX_AVAILABLE, 1, row++);
        form.add(new Label("Contractor"), 0, row);
        form.add(CHECK_BOX_CONTRACTOR, 1, row++);
        form.add(new Label("Workload"), 0, row);
        form.add(SLIDER_WORKLOAD, 1, row++);

        HBox buttonBar = HBoxBuilder.create().spacing(5).children(SAVE, RESET, DELETE).build();
        form.add(buttonBar, 1, row);

        setupBinding();
        addClientSideAction();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Team Members in JavaFX");
        scene.getStylesheets().add("/team.css");


        clientDolphin.send(CMD_INIT, new OnFinishedHandlerAdapter() {
            @Override public void onFinished(List<ClientPresentationModel> presentationModels) {
                stage.show();
                longPoll();
            }
        });
    }

    private void longPoll() {
        pollerDolphin.send(CMD_POLL, new OnFinishedHandlerAdapter(){
            @Override public void onFinished(List<ClientPresentationModel> presentationModels) {
                // now there may be something interesting for us
                clientDolphin.send(CMD_UPDATE, new OnFinishedHandlerAdapter() {
                    @Override
                    public void onFinished(List<ClientPresentationModel> presentationModels) {
                        longPoll();
                    }
                });
            }
        });
    }

    private Image getImage(String function) {
        if ("".equals(function)) function = "Unselected";
        if (!lazyImageCache.containsKey(function)) {
            lazyImageCache.put(function, new Image("http://people.canoo.com/mittie/rolePics/" + function + ".jpg"));
        }
        return lazyImageCache.get(function);
    }

    final   Duration           imageTransitionDuration = Duration.millis(300);
    private ParallelTransition imageTransition         = ParallelTransitionBuilder.create().children(
        TranslateTransitionBuilder.create().duration(imageTransitionDuration).node(IMAGE_ANIM).fromX(130).toX(0).build(),
        TranslateTransitionBuilder.create().duration(imageTransitionDuration).node(IMAGE_FUNCTION).fromX(0).toX(-130).build(),
        ScaleTransitionBuilder.create().duration(imageTransitionDuration).node(IMAGE_ANIM).fromX(0).toX(1).fromY(0.5).toY(1).build(),
        ScaleTransitionBuilder.create().duration(imageTransitionDuration).node(IMAGE_FUNCTION).fromX(1).toX(0).fromY(1).toY(0.5).build()
    ).onFinished(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            IMAGE_FUNCTION.setImage(IMAGE_ANIM.getImage());
            IMAGE_FUNCTION.setScaleX(1);
            IMAGE_FUNCTION.setScaleY(1);
            IMAGE_FUNCTION.setTranslateX(0);
        }
    }).build();

    private void setupBinding() {

        // detail binding

        bind("text").of(FIELD_FIRST_NAME).to(ATT_FIRSTNAME).of(teamMemberMold);
        bind(ATT_FIRSTNAME).of(teamMemberMold).to("text").of(FIELD_FIRST_NAME);

        bind("text").of(FIELD_LAST_NAME).to(ATT_LASTNAME).of(teamMemberMold);
        bind(ATT_LASTNAME).of(teamMemberMold).to("text").of(FIELD_LAST_NAME);

        COMBO_BOX_FUNCTION.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                teamMemberMold.getAt(ATT_FUNCTION).setValue(COMBO_BOX_FUNCTION.getValue());
            }
        });
        teamMemberMold.getAt(ATT_FUNCTION).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                COMBO_BOX_FUNCTION.setValue(evt.getNewValue().toString());
                IMAGE_ANIM.setImage(getImage(evt.getNewValue().toString()));
                imageTransition.play();
            }
        });

        bind("selected").of(CHECK_BOX_AVAILABLE).to(ATT_AVAILABLE).of(teamMemberMold);
        bind(ATT_AVAILABLE).of(teamMemberMold).to("selected").of(CHECK_BOX_AVAILABLE);

        bind("selected").of(CHECK_BOX_CONTRACTOR).to(ATT_CONTRACTOR).of(teamMemberMold);
        bind(ATT_CONTRACTOR).of(teamMemberMold).to("selected").of(CHECK_BOX_CONTRACTOR);

        bind("value").of(SLIDER_WORKLOAD).to(ATT_WORKLOAD).of(teamMemberMold, new Converter() {
            @Override
            public Object convert(Object value) {
                return ((Double) value).intValue();
            } // round to int
        });
        bind(ATT_WORKLOAD).of(teamMemberMold).to("value").of(SLIDER_WORKLOAD, new Converter() {
            @Override
            public Object convert(Object value) {
                if (SLIDER_WORKLOAD.isPressed()) return SLIDER_WORKLOAD.getValue(); // do not update while we work
                if (value instanceof String) return Integer.valueOf((String) value);
                return ((Number) value).intValue();
            } // round to int
        });

        // dirty status in detail view

        // todo: could go into dolphin
        teamMemberMold.addPropertyChangeListener("dirty", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ((Boolean) evt.getNewValue()) {
                    form.getStyleClass().add("detail-dirty");
                } else {
                    form.getStyleClass().removeAll("detail-dirty");
                }
            }
        });

        // todo: could go into dolphin
        final Converter inverter = new Converter() {
            @Override
            public Object convert(Object value) {
                return (!(Boolean) value);
            }
        };

        bindInfo("dirty").of(teamMemberMold).using(inverter).to("disable").of(SAVE);
        bindInfo("dirty").of(teamMemberMold).using(inverter).to("disable").of(RESET);

        // list binding

        clientDolphin.addModelStoreListener(TYPE_TEAM_MEMBER, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                final PresentationModel pm = event.getPresentationModel();
                if (event.getType().equals(ModelStoreEvent.Type.ADDED)) {
                    teamMembers.add(pm);
                }
                if (event.getType() == ModelStoreEvent.Type.REMOVED) {
                    teamMembers.remove(pm);
                }
            }
        });

        // selection Binding

        // keep selectedPmId in sync with table selection
        selectedPmId.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (null == evt.getNewValue()) {
                    table.getSelectionModel().clearSelection();
                    return;
                }
                for (PresentationModel model : teamMembers) {
                    if (model.getId().equals(evt.getNewValue())) {
                        table.getSelectionModel().select(model);
                        break;
                    }
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object old, Object newSelection) {
                if (null != newSelection) {
                    ClientPresentationModel selectedPm = (ClientPresentationModel) newSelection;
                    selectedPmId.setValue(selectedPm.getId());
                    clientDolphin.apply(selectedPm).to(teamMemberMold);
                    form.setDisable(false);
                    form.setEffect(null);
                } else {
                    selectedPmId.setValue(null);
                    clientDolphin.apply(blankMold).to(teamMemberMold);
                    form.setDisable(true);
                    form.setEffect(new BoxBlur());
                }
            }
        });
    }

    private void addClientSideAction() {

        ADD_BUTTON.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ADD_BUTTON.setDisable(true);
                clientDolphin.send(CMD_ADD, new OnFinishedHandlerAdapter() {
                    @Override
                    public void onFinished(List<ClientPresentationModel> presentationModels) {
                        ADD_BUTTON.setDisable(false);
                    }
                });
            }
        });

        SAVE.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                SAVE.setDisable(true);
                clientDolphin.send(CMD_SAVE);
            }
        });

        RESET.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                teamMemberMold.reset();
            }
        });

        DELETE.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DELETE.setDisable(true);
                clientDolphin.send(CMD_REMOVE, new OnFinishedHandlerAdapter() {
                    @Override
                    public void onFinished(List<ClientPresentationModel> presentationModels) {
                        DELETE.setDisable(false);
                    }
                });
            }
        });
    }
}