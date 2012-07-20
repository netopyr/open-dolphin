package com.canoo.dolphin.logo;

import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;


public class DolphinLogoSkin extends SkinBase<DolphinLogo, DolphinLogoBehavior> {
    private DolphinLogo control;
    private boolean     square;
    private boolean     keepAspect;
    private boolean     isDirty;
    private boolean     initialized;
    private Group       logo;


    // ******************** Constructors **************************************
    public DolphinLogoSkin(final DolphinLogo CONTROL) {
        super(CONTROL, new DolphinLogoBehavior(CONTROL));
        control     = CONTROL;
        square      = control.isSquare();
        keepAspect  = control.isKeepAspect();
        initialized = false;
        isDirty     = false;
        logo      = new Group();

        init();
    }

    private void init() {
        if (control.getPrefWidth() < 0 | control.getPrefHeight() < 0) {
            control.setPrefSize(400, 257);
        }

        // Register listeners

        this.registerChangeListener(control.prefWidthProperty(), "PREF_WIDTH");
        this.registerChangeListener(control.prefHeightProperty(), "PREF_HEIGHT");

        initialized = true;
        paint();
    }


    // ******************** Methods *******************************************
    @Override protected void handleControlPropertyChanged(final String PROPERTY) {
        super.handleControlPropertyChanged(PROPERTY);
        if ("PREF_WIDTH".equals(PROPERTY)) {
            paint();
        } else if ("PREF_HEIGHT".equals(PROPERTY)) {
            paint();
        }
    }

    public final void paint() {
        if (!initialized) {
            init();
        }
        getChildren().clear();
        drawLogo();

        getChildren().addAll(logo);
    }

    @Override public void layoutChildren() {
        if (isDirty) {
            paint();
            isDirty = false;
        }
        super.layoutChildren();
    }

    @Override public final DolphinLogo getSkinnable() {
        return control;
    }

    @Override public final void dispose() {
        control = null;
    }

    @Override protected double computePrefWidth(final double PREF_WIDTH) {
        double prefWidth = 400;
        if (PREF_WIDTH != -1) {
            prefWidth = Math.max(0, PREF_WIDTH - getInsets().getLeft() - getInsets().getRight());
        }
        return super.computePrefWidth(prefWidth);
    }

    @Override protected double computePrefHeight(final double PREF_HEIGHT) {
        double prefHeight = 257;
        if (PREF_HEIGHT != -1) {
            prefHeight = Math.max(0, PREF_HEIGHT - getInsets().getTop() - getInsets().getBottom());
        }
        return super.computePrefHeight(prefHeight);
    }

	@Override protected double computeMinWidth(final double MIN_WIDTH) {
	    return super.computeMinWidth(Math.max(400, MIN_WIDTH - getInsets().getLeft() - getInsets().getRight()));
	}

	@Override protected double computeMinHeight(final double MIN_HEIGHT) {
	    return super.computeMinHeight(Math.max(257, MIN_HEIGHT - getInsets().getTop() - getInsets().getBottom()));
	}

	@Override protected double computeMaxWidth(final double MAX_WIDTH) {
	    return super.computeMaxWidth(Math.max(400, MAX_WIDTH - getInsets().getLeft() - getInsets().getRight()));
	}

	@Override protected double computeMaxHeight(final double MAX_HEIGHT) {
	    return super.computeMaxHeight(Math.max(257, MAX_HEIGHT - getInsets().getTop() - getInsets().getBottom()));
	}

    private final String createCssColor(final Color COLOR) {
        final StringBuilder CSS_COLOR = new StringBuilder(19);
        CSS_COLOR.append("rgba(");
        CSS_COLOR.append((int) (COLOR.getRed() * 255));
        CSS_COLOR.append(", ");
        CSS_COLOR.append((int) (COLOR.getGreen() * 255));
        CSS_COLOR.append(", ");
        CSS_COLOR.append((int) (COLOR.getBlue() * 255));
        CSS_COLOR.append(", ");
        CSS_COLOR.append(COLOR.getOpacity());
        CSS_COLOR.append(");");
        return CSS_COLOR.toString();
    }


    // ******************** Drawing related ***********************************    
    public final void drawLogo() {
        final double SIZE   = control.getPrefWidth() < control.getPrefHeight() ? control.getPrefWidth() : control.getPrefHeight();
        final double WIDTH  = square ? SIZE : control.getPrefWidth();
        final double HEIGHT = square ? SIZE : control.getPrefHeight();

        logo.getChildren().clear();

        final Shape IBOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
        IBOUNDS.setOpacity(0.0);
        logo.getChildren().add(IBOUNDS);

        final Path PATH = new Path();
        PATH.setFillRule(FillRule.EVEN_ODD);
        PATH.getElements().add(new MoveTo(0.5275 * WIDTH, 0.019455252918287938 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.525 * WIDTH, 0.023346303501945526 * HEIGHT,
                                                0.51 * WIDTH, 0.03501945525291829 * HEIGHT,
                                                0.51 * WIDTH, 0.04669260700389105 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.51 * WIDTH, 0.054474708171206226 * HEIGHT,
                                                0.5275 * WIDTH, 0.0622568093385214 * HEIGHT,
                                                0.53 * WIDTH, 0.0622568093385214 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.5325 * WIDTH, 0.0622568093385214 * HEIGHT,
                                                0.54 * WIDTH, 0.058365758754863814 * HEIGHT,
                                                0.5425 * WIDTH, 0.054474708171206226 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.565 * WIDTH, 0.027237354085603113 * HEIGHT,
                                                0.605 * WIDTH, 0.0311284046692607 * HEIGHT,
                                                0.6275 * WIDTH, 0.0311284046692607 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.665 * WIDTH, 0.03501945525291829 * HEIGHT,
                                                0.715 * WIDTH, 0.05058365758754864 * HEIGHT,
                                                0.7175 * WIDTH, 0.04669260700389105 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.71 * WIDTH, 0.042801556420233464 * HEIGHT,
                                                0.67 * WIDTH, 0.019455252918287938 * HEIGHT,
                                                0.665 * WIDTH, 0.019455252918287938 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.655 * WIDTH, 0.01556420233463035 * HEIGHT,
                                                0.62 * WIDTH, 0.0,
                                                0.5975 * WIDTH, 0.0));
        PATH.getElements().add(new CubicCurveTo(0.59 * WIDTH, 0.0,
                                                0.555 * WIDTH, 0.0,
                                                0.5275 * WIDTH, 0.019455252918287938 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.2125 * WIDTH, 0.05058365758754864 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.195 * WIDTH, 0.06614785992217899 * HEIGHT,
                                                0.1575 * WIDTH, 0.10505836575875487 * HEIGHT,
                                                0.1475 * WIDTH, 0.1245136186770428 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.14 * WIDTH, 0.13229571984435798 * HEIGHT,
                                                0.125 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                0.115 * WIDTH, 0.17120622568093385 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0975 * WIDTH, 0.1867704280155642 * HEIGHT,
                                                0.04 * WIDTH, 0.17120622568093385 * HEIGHT,
                                                0.0175 * WIDTH, 0.17898832684824903 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.015 * WIDTH, 0.17898832684824903 * HEIGHT,
                                                0.0, 0.19066147859922178 * HEIGHT,
                                                0.0, 0.19844357976653695 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(-0.0025 * WIDTH, 0.22178988326848248 * HEIGHT,
                                                0.01 * WIDTH, 0.245136186770428 * HEIGHT,
                                                0.0425 * WIDTH, 0.26459143968871596 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0525 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                0.0725 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                0.1 * WIDTH, 0.27626459143968873 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.105 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                0.115 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                0.1175 * WIDTH, 0.2723735408560311 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1175 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                0.1325 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.1325 * WIDTH, 0.27626459143968873 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1275 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.1 * WIDTH, 0.29961089494163423 * HEIGHT,
                                                0.0875 * WIDTH, 0.29961089494163423 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0875 * WIDTH, 0.29961089494163423 * HEIGHT,
                                                0.035 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.0325 * WIDTH, 0.28793774319066145 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0275 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.0125 * WIDTH, 0.2918287937743191 * HEIGHT,
                                                0.0125 * WIDTH, 0.3035019455252918 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0125 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.015 * WIDTH, 0.32684824902723736 * HEIGHT,
                                                0.025 * WIDTH, 0.33852140077821014 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0625 * WIDTH, 0.377431906614786 * HEIGHT,
                                                0.1 * WIDTH, 0.3852140077821012 * HEIGHT,
                                                0.1375 * WIDTH, 0.38910505836575876 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.155 * WIDTH, 0.38910505836575876 * HEIGHT,
                                                0.1725 * WIDTH, 0.39299610894941633 * HEIGHT,
                                                0.1875 * WIDTH, 0.3968871595330739 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.195 * WIDTH, 0.3968871595330739 * HEIGHT,
                                                0.2 * WIDTH, 0.40077821011673154 * HEIGHT,
                                                0.205 * WIDTH, 0.4046692607003891 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.215 * WIDTH, 0.4085603112840467 * HEIGHT,
                                                0.235 * WIDTH, 0.42412451361867703 * HEIGHT,
                                                0.245 * WIDTH, 0.4357976653696498 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.25 * WIDTH, 0.44357976653696496 * HEIGHT,
                                                0.265 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                0.2725 * WIDTH, 0.4591439688715953 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.28 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                0.2925 * WIDTH, 0.45136186770428016 * HEIGHT,
                                                0.2925 * WIDTH, 0.4396887159533074 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2925 * WIDTH, 0.4280155642023346 * HEIGHT,
                                                0.28 * WIDTH, 0.4163424124513619 * HEIGHT,
                                                0.275 * WIDTH, 0.4085603112840467 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.265 * WIDTH, 0.3968871595330739 * HEIGHT,
                                                0.255 * WIDTH, 0.38910505836575876 * HEIGHT,
                                                0.245 * WIDTH, 0.38132295719844356 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2325 * WIDTH, 0.3735408560311284 * HEIGHT,
                                                0.21 * WIDTH, 0.3657587548638132 * HEIGHT,
                                                0.2075 * WIDTH, 0.3657587548638132 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2075 * WIDTH, 0.3657587548638132 * HEIGHT,
                                                0.1675 * WIDTH, 0.3657587548638132 * HEIGHT,
                                                0.1675 * WIDTH, 0.3657587548638132 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.16 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                0.155 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                0.15 * WIDTH, 0.36964980544747084 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.15 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                0.1475 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                0.1125 * WIDTH, 0.36964980544747084 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                0.0875 * WIDTH, 0.3657587548638132 * HEIGHT,
                                                0.0725 * WIDTH, 0.36186770428015563 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0725 * WIDTH, 0.36186770428015563 * HEIGHT,
                                                0.0275 * WIDTH, 0.3424124513618677 * HEIGHT,
                                                0.0275 * WIDTH, 0.3151750972762646 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0275 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.03 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.035 * WIDTH, 0.30739299610894943 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0375 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.0575 * WIDTH, 0.311284046692607 * HEIGHT,
                                                0.07 * WIDTH, 0.3151750972762646 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.09 * WIDTH, 0.3151750972762646 * HEIGHT,
                                                0.0925 * WIDTH, 0.3151750972762646 * HEIGHT,
                                                0.0925 * WIDTH, 0.3151750972762646 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1 * WIDTH, 0.311284046692607 * HEIGHT,
                                                0.1125 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.12 * WIDTH, 0.3035019455252918 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.135 * WIDTH, 0.2918287937743191 * HEIGHT,
                                                0.145 * WIDTH, 0.2801556420233463 * HEIGHT,
                                                0.155 * WIDTH, 0.2801556420233463 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1575 * WIDTH, 0.2801556420233463 * HEIGHT,
                                                0.185 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.1875 * WIDTH, 0.28793774319066145 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.19 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.1975 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.2025 * WIDTH, 0.28793774319066145 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.195 * WIDTH, 0.2840466926070039 * HEIGHT,
                                                0.19 * WIDTH, 0.2840466926070039 * HEIGHT,
                                                0.1825 * WIDTH, 0.2801556420233463 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.175 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                0.17 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.16 * WIDTH, 0.26459143968871596 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1525 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.1375 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.135 * WIDTH, 0.2607003891050584 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1325 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.1275 * WIDTH, 0.26459143968871596 * HEIGHT,
                                                0.1225 * WIDTH, 0.26459143968871596 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.08 * WIDTH, 0.26459143968871596 * HEIGHT,
                                                0.075 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.07 * WIDTH, 0.2607003891050584 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.065 * WIDTH, 0.25680933852140075 * HEIGHT,
                                                0.0125 * WIDTH, 0.245136186770428 * HEIGHT,
                                                0.0125 * WIDTH, 0.21011673151750973 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.0125 * WIDTH, 0.20622568093385213 * HEIGHT,
                                                0.0175 * WIDTH, 0.20233463035019456 * HEIGHT,
                                                0.0225 * WIDTH, 0.20233463035019456 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.035 * WIDTH, 0.19455252918287938 * HEIGHT,
                                                0.0775 * WIDTH, 0.19844357976653695 * HEIGHT,
                                                0.095 * WIDTH, 0.19844357976653695 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.12 * WIDTH, 0.19844357976653695 * HEIGHT,
                                                0.135 * WIDTH, 0.17898832684824903 * HEIGHT,
                                                0.145 * WIDTH, 0.16342412451361868 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.1625 * WIDTH, 0.13618677042801555 * HEIGHT,
                                                0.1875 * WIDTH, 0.11284046692607004 * HEIGHT,
                                                0.2075 * WIDTH, 0.09727626459143969 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.22 * WIDTH, 0.08949416342412451 * HEIGHT,
                                                0.255 * WIDTH, 0.07003891050583658 * HEIGHT,
                                                0.3025 * WIDTH, 0.07003891050583658 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.3025 * WIDTH, 0.07003891050583658 * HEIGHT,
                                                0.375 * WIDTH, 0.07392996108949416 * HEIGHT,
                                                0.3975 * WIDTH, 0.08171206225680934 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.505 * WIDTH, 0.11673151750972763 * HEIGHT,
                                                0.585 * WIDTH, 0.1828793774319066 * HEIGHT,
                                                0.6075 * WIDTH, 0.21011673151750973 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.6375 * WIDTH, 0.245136186770428 * HEIGHT,
                                                0.6725 * WIDTH, 0.26459143968871596 * HEIGHT,
                                                0.73 * WIDTH, 0.4046692607003891 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.78 * WIDTH, 0.5252918287937743 * HEIGHT,
                                                0.795 * WIDTH, 0.6070038910505836 * HEIGHT,
                                                0.805 * WIDTH, 0.6536964980544747 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.805 * WIDTH, 0.6575875486381323 * HEIGHT,
                                                0.8 * WIDTH, 0.603112840466926 * HEIGHT,
                                                0.795 * WIDTH, 0.5680933852140078 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.77 * WIDTH, 0.4474708171206226 * HEIGHT,
                                                0.755 * WIDTH, 0.39299610894941633 * HEIGHT,
                                                0.7275 * WIDTH, 0.3346303501945525 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.6925 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.6375 * WIDTH, 0.16731517509727625 * HEIGHT,
                                                0.5375 * WIDTH, 0.0933852140077821 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.52 * WIDTH, 0.07782101167315175 * HEIGHT,
                                                0.4225 * WIDTH, 0.019455252918287938 * HEIGHT,
                                                0.34 * WIDTH, 0.019455252918287938 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.315 * WIDTH, 0.019455252918287938 * HEIGHT,
                                                0.245 * WIDTH, 0.027237354085603113 * HEIGHT,
                                                0.2125 * WIDTH, 0.05058365758754864 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.6975 * WIDTH, 0.07392996108949416 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.69 * WIDTH, 0.08171206225680934 * HEIGHT,
                                                0.645 * WIDTH, 0.10116731517509728 * HEIGHT,
                                                0.64 * WIDTH, 0.14785992217898833 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.64 * WIDTH, 0.1517509727626459 * HEIGHT,
                                                0.645 * WIDTH, 0.15953307392996108 * HEIGHT,
                                                0.6475 * WIDTH, 0.16342412451361868 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.6475 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                0.6525 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                0.6525 * WIDTH, 0.16342412451361868 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.6575 * WIDTH, 0.1245136186770428 * HEIGHT,
                                                0.6875 * WIDTH, 0.09727626459143969 * HEIGHT,
                                                0.705 * WIDTH, 0.07392996108949416 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.705 * WIDTH, 0.07392996108949416 * HEIGHT,
                                                0.6975 * WIDTH, 0.07392996108949416 * HEIGHT,
                                                0.6975 * WIDTH, 0.07392996108949416 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.2175 * WIDTH, 0.22957198443579765 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.21 * WIDTH, 0.22957198443579765 * HEIGHT,
                                                0.2075 * WIDTH, 0.23735408560311283 * HEIGHT,
                                                0.2075 * WIDTH, 0.24124513618677043 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2075 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.225 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.23 * WIDTH, 0.26848249027237353 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2375 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.25 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.25 * WIDTH, 0.2490272373540856 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.25 * WIDTH, 0.23735408560311283 * HEIGHT,
                                                0.2475 * WIDTH, 0.23735408560311283 * HEIGHT,
                                                0.2375 * WIDTH, 0.22957198443579765 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2375 * WIDTH, 0.22957198443579765 * HEIGHT,
                                                0.2325 * WIDTH, 0.22957198443579765 * HEIGHT,
                                                0.2325 * WIDTH, 0.22957198443579765 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2325 * WIDTH, 0.23346303501945526 * HEIGHT,
                                                0.2325 * WIDTH, 0.23346303501945526 * HEIGHT,
                                                0.2325 * WIDTH, 0.23346303501945526 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2325 * WIDTH, 0.24124513618677043 * HEIGHT,
                                                0.23 * WIDTH, 0.24124513618677043 * HEIGHT,
                                                0.2275 * WIDTH, 0.24124513618677043 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2275 * WIDTH, 0.24124513618677043 * HEIGHT,
                                                0.2225 * WIDTH, 0.23735408560311283 * HEIGHT,
                                                0.2225 * WIDTH, 0.22957198443579765 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.2225 * WIDTH, 0.22957198443579765 * HEIGHT,
                                                0.2175 * WIDTH, 0.22957198443579765 * HEIGHT,
                                                0.2175 * WIDTH, 0.22957198443579765 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.41 * WIDTH, 0.44357976653696496 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4125 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                0.4225 * WIDTH, 0.47470817120622566 * HEIGHT,
                                                0.4225 * WIDTH, 0.4785992217898833 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4375 * WIDTH, 0.5097276264591439 * HEIGHT,
                                                0.4575 * WIDTH, 0.5252918287937743 * HEIGHT,
                                                0.4725 * WIDTH, 0.5408560311284046 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4825 * WIDTH, 0.5525291828793775 * HEIGHT,
                                                0.495 * WIDTH, 0.5719844357976653 * HEIGHT,
                                                0.5025 * WIDTH, 0.5914396887159533 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.51 * WIDTH, 0.6070038910505836 * HEIGHT,
                                                0.5125 * WIDTH, 0.622568093385214 * HEIGHT,
                                                0.5175 * WIDTH, 0.6381322957198443 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.525 * WIDTH, 0.6342412451361867 * HEIGHT,
                                                0.525 * WIDTH, 0.6303501945525292 * HEIGHT,
                                                0.525 * WIDTH, 0.622568093385214 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.525 * WIDTH, 0.6186770428015564 * HEIGHT,
                                                0.52 * WIDTH, 0.5914396887159533 * HEIGHT,
                                                0.5175 * WIDTH, 0.5914396887159533 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.5 * WIDTH, 0.5408560311284046 * HEIGHT,
                                                0.4825 * WIDTH, 0.5214007782101168 * HEIGHT,
                                                0.465 * WIDTH, 0.5019455252918288 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4475 * WIDTH, 0.48249027237354086 * HEIGHT,
                                                0.4325 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                0.425 * WIDTH, 0.44357976653696496 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4175 * WIDTH, 0.42023346303501946 * HEIGHT,
                                                0.4125 * WIDTH, 0.4085603112840467 * HEIGHT,
                                                0.41 * WIDTH, 0.4085603112840467 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.405 * WIDTH, 0.4163424124513619 * HEIGHT,
                                                0.4075 * WIDTH, 0.4280155642023346 * HEIGHT,
                                                0.41 * WIDTH, 0.44357976653696496 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.3 * WIDTH, 0.45136186770428016 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.29 * WIDTH, 0.48638132295719844 * HEIGHT,
                                                0.295 * WIDTH, 0.5136186770428015 * HEIGHT,
                                                0.31 * WIDTH, 0.5408560311284046 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.31 * WIDTH, 0.5447470817120622 * HEIGHT,
                                                0.3225 * WIDTH, 0.5642023346303502 * HEIGHT,
                                                0.33 * WIDTH, 0.5719844357976653 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.355 * WIDTH, 0.603112840466926 * HEIGHT,
                                                0.3875 * WIDTH, 0.622568093385214 * HEIGHT,
                                                0.415 * WIDTH, 0.6303501945525292 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.425 * WIDTH, 0.6342412451361867 * HEIGHT,
                                                0.4475 * WIDTH, 0.6381322957198443 * HEIGHT,
                                                0.4525 * WIDTH, 0.6381322957198443 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4525 * WIDTH, 0.6381322957198443 * HEIGHT,
                                                0.47 * WIDTH, 0.6381322957198443 * HEIGHT,
                                                0.47 * WIDTH, 0.6381322957198443 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4775 * WIDTH, 0.6381322957198443 * HEIGHT,
                                                0.4925 * WIDTH, 0.6381322957198443 * HEIGHT,
                                                0.5 * WIDTH, 0.622568093385214 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.49 * WIDTH, 0.622568093385214 * HEIGHT,
                                                0.47 * WIDTH, 0.6070038910505836 * HEIGHT,
                                                0.46 * WIDTH, 0.603112840466926 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.43 * WIDTH, 0.5875486381322957 * HEIGHT,
                                                0.3925 * WIDTH, 0.5603112840466926 * HEIGHT,
                                                0.365 * WIDTH, 0.5175097276264592 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.3575 * WIDTH, 0.5058365758754864 * HEIGHT,
                                                0.335 * WIDTH, 0.4669260700389105 * HEIGHT,
                                                0.335 * WIDTH, 0.45136186770428016 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.335 * WIDTH, 0.4396887159533074 * HEIGHT,
                                                0.3375 * WIDTH, 0.44357976653696496 * HEIGHT,
                                                0.34 * WIDTH, 0.43190661478599224 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.3375 * WIDTH, 0.4280155642023346 * HEIGHT,
                                                0.3325 * WIDTH, 0.4280155642023346 * HEIGHT,
                                                0.33 * WIDTH, 0.4280155642023346 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.3175 * WIDTH, 0.4280155642023346 * HEIGHT,
                                                0.305 * WIDTH, 0.43190661478599224 * HEIGHT,
                                                0.3 * WIDTH, 0.45136186770428016 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.48 * WIDTH, 0.4708171206225681 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.48 * WIDTH, 0.48249027237354086 * HEIGHT,
                                                0.4825 * WIDTH, 0.490272373540856 * HEIGHT,
                                                0.4975 * WIDTH, 0.5097276264591439 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.5025 * WIDTH, 0.5175097276264592 * HEIGHT,
                                                0.5225 * WIDTH, 0.5330739299610895 * HEIGHT,
                                                0.53 * WIDTH, 0.5369649805447471 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.535 * WIDTH, 0.5408560311284046 * HEIGHT,
                                                0.59 * WIDTH, 0.5642023346303502 * HEIGHT,
                                                0.6275 * WIDTH, 0.5914396887159533 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.6525 * WIDTH, 0.6108949416342413 * HEIGHT,
                                                0.6825 * WIDTH, 0.642023346303502 * HEIGHT,
                                                0.7025 * WIDTH, 0.6731517509727627 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7 * WIDTH, 0.6342412451361867 * HEIGHT,
                                                0.69 * WIDTH, 0.6147859922178989 * HEIGHT,
                                                0.6725 * WIDTH, 0.5875486381322957 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.66 * WIDTH, 0.5680933852140078 * HEIGHT,
                                                0.6375 * WIDTH, 0.5330739299610895 * HEIGHT,
                                                0.605 * WIDTH, 0.5097276264591439 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.59 * WIDTH, 0.4980544747081712 * HEIGHT,
                                                0.55 * WIDTH, 0.4708171206225681 * HEIGHT,
                                                0.5325 * WIDTH, 0.46303501945525294 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.5225 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                0.5075 * WIDTH, 0.45525291828793774 * HEIGHT,
                                                0.4975 * WIDTH, 0.45525291828793774 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.4925 * WIDTH, 0.45525291828793774 * HEIGHT,
                                                0.48 * WIDTH, 0.46303501945525294 * HEIGHT,
                                                0.48 * WIDTH, 0.4708171206225681 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.8175 * WIDTH, 0.6964980544747081 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.815 * WIDTH, 0.7003891050583657 * HEIGHT,
                                                0.8075 * WIDTH, 0.708171206225681 * HEIGHT,
                                                0.8075 * WIDTH, 0.7120622568093385 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.8075 * WIDTH, 0.7120622568093385 * HEIGHT,
                                                0.8125 * WIDTH, 0.7120622568093385 * HEIGHT,
                                                0.8125 * WIDTH, 0.7120622568093385 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.8175 * WIDTH, 0.7120622568093385 * HEIGHT,
                                                0.8525 * WIDTH, 0.7198443579766537 * HEIGHT,
                                                0.87 * WIDTH, 0.7276264591439688 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.885 * WIDTH, 0.7315175097276264 * HEIGHT,
                                                0.8975 * WIDTH, 0.7354085603112841 * HEIGHT,
                                                0.9075 * WIDTH, 0.7431906614785992 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.9125 * WIDTH, 0.7470817120622568 * HEIGHT,
                                                0.9475 * WIDTH, 0.7665369649805448 * HEIGHT,
                                                0.965 * WIDTH, 0.7859922178988327 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.9675 * WIDTH, 0.7898832684824902 * HEIGHT,
                                                0.9375 * WIDTH, 0.77431906614786 * HEIGHT,
                                                0.9225 * WIDTH, 0.77431906614786 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.8925 * WIDTH, 0.77431906614786 * HEIGHT,
                                                0.865 * WIDTH, 0.77431906614786 * HEIGHT,
                                                0.8275 * WIDTH, 0.8015564202334631 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7925 * WIDTH, 0.8404669260700389 * HEIGHT,
                                                0.7825 * WIDTH, 0.867704280155642 * HEIGHT,
                                                0.7775 * WIDTH, 0.8793774319066148 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7675 * WIDTH, 0.9027237354085603 * HEIGHT,
                                                0.7525 * WIDTH, 0.9571984435797666 * HEIGHT,
                                                0.7525 * WIDTH, 0.9610894941634242 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7525 * WIDTH, 0.9649805447470817 * HEIGHT,
                                                0.75 * WIDTH, 0.9766536964980544 * HEIGHT,
                                                0.755 * WIDTH, 0.980544747081712 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7575 * WIDTH, 0.9844357976653697 * HEIGHT,
                                                0.76 * WIDTH, 0.9766536964980544 * HEIGHT,
                                                0.7625 * WIDTH, 0.9766536964980544 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.765 * WIDTH, 0.9688715953307393 * HEIGHT,
                                                0.7675 * WIDTH, 0.9610894941634242 * HEIGHT,
                                                0.77 * WIDTH, 0.953307392996109 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7975 * WIDTH, 0.8715953307392996 * HEIGHT,
                                                0.825 * WIDTH, 0.8365758754863813 * HEIGHT,
                                                0.8675 * WIDTH, 0.8132295719844358 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.885 * WIDTH, 0.8054474708171206 * HEIGHT,
                                                0.9075 * WIDTH, 0.8015564202334631 * HEIGHT,
                                                0.9475 * WIDTH, 0.8054474708171206 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.9475 * WIDTH, 0.8054474708171206 * HEIGHT,
                                                WIDTH, 0.8249027237354085 * HEIGHT,
                                                WIDTH, 0.8210116731517509 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(WIDTH, 0.8132295719844358 * HEIGHT,
                                                0.9825 * WIDTH, 0.7821011673151751 * HEIGHT,
                                                0.96 * WIDTH, 0.7587548638132295 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.9275 * WIDTH, 0.7237354085603113 * HEIGHT,
                                                0.87 * WIDTH, 0.6964980544747081 * HEIGHT,
                                                0.835 * WIDTH, 0.6964980544747081 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.8325 * WIDTH, 0.6964980544747081 * HEIGHT,
                                                0.8225 * WIDTH, 0.6964980544747081 * HEIGHT,
                                                0.8175 * WIDTH, 0.6964980544747081 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        PATH.getElements().add(new MoveTo(0.7275 * WIDTH, 0.77431906614786 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7275 * WIDTH, 0.77431906614786 * HEIGHT,
                                                0.7325 * WIDTH, 0.8249027237354085 * HEIGHT,
                                                0.7325 * WIDTH, 0.8287937743190662 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7325 * WIDTH, 0.8326848249027238 * HEIGHT,
                                                0.7325 * WIDTH, 0.8482490272373541 * HEIGHT,
                                                0.7325 * WIDTH, 0.8560311284046692 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7325 * WIDTH, 0.8599221789883269 * HEIGHT,
                                                0.73 * WIDTH, 0.8793774319066148 * HEIGHT,
                                                0.73 * WIDTH, 0.8910505836575876 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.72 * WIDTH, 0.9649805447470817 * HEIGHT,
                                                0.6975 * WIDTH, 0.9844357976653697 * HEIGHT,
                                                0.7 * WIDTH, HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7025 * WIDTH, HEIGHT,
                                                0.71 * WIDTH, 0.9961089494163424 * HEIGHT,
                                                0.7125 * WIDTH, 0.9883268482490273 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.715 * WIDTH, 0.9883268482490273 * HEIGHT,
                                                0.73 * WIDTH, 0.9455252918287937 * HEIGHT,
                                                0.7325 * WIDTH, 0.9455252918287937 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7375 * WIDTH, 0.9260700389105059 * HEIGHT,
                                                0.7425 * WIDTH, 0.9027237354085603 * HEIGHT,
                                                0.7475 * WIDTH, 0.8832684824902723 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7475 * WIDTH, 0.8754863813229572 * HEIGHT,
                                                0.75 * WIDTH, 0.8638132295719845 * HEIGHT,
                                                0.75 * WIDTH, 0.8521400778210116 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.75 * WIDTH, 0.8482490272373541 * HEIGHT,
                                                0.7475 * WIDTH, 0.8171206225680934 * HEIGHT,
                                                0.75 * WIDTH, 0.8132295719844358 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.75 * WIDTH, 0.8132295719844358 * HEIGHT,
                                                0.7475 * WIDTH, 0.8132295719844358 * HEIGHT,
                                                0.7475 * WIDTH, 0.8093385214007782 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.7475 * WIDTH, 0.8054474708171206 * HEIGHT,
                                                0.745 * WIDTH, 0.7587548638132295 * HEIGHT,
                                                0.7325 * WIDTH, 0.7587548638132295 * HEIGHT));
        PATH.getElements().add(new CubicCurveTo(0.73 * WIDTH, 0.7587548638132295 * HEIGHT,
                                                0.7275 * WIDTH, 0.7665369649805448 * HEIGHT,
                                                0.7275 * WIDTH, 0.77431906614786 * HEIGHT));
        PATH.getElements().add(new ClosePath());
        //PATH.getStyleClass().add("layer_1-path");
        final Paint PATH_FILL = Color.BLACK;
        PATH.setFill(PATH_FILL);
        PATH.setStroke(null);

        final Path PATH1 = new Path();
        PATH1.setFillRule(FillRule.EVEN_ODD);
        PATH1.getElements().add(new MoveTo(0.7125 * WIDTH, 0.4085603112840467 * HEIGHT));
        PATH1.getElements().add(new CubicCurveTo(0.6525 * WIDTH, 0.29961089494163423 * HEIGHT,
                                                 0.57 * WIDTH, 0.21011673151750973 * HEIGHT,
                                                 0.46 * WIDTH, 0.14785992217898833 * HEIGHT));
        PATH1.getElements().add(new CubicCurveTo(0.41 * WIDTH, 0.12062256809338522 * HEIGHT,
                                                 0.3525 * WIDTH, 0.10505836575875487 * HEIGHT,
                                                 0.3075 * WIDTH, 0.10116731517509728 * HEIGHT));
        PATH1.getElements().add(new CubicCurveTo(0.3725 * WIDTH, 0.07003891050583658 * HEIGHT,
                                                 0.4525 * WIDTH, 0.11284046692607004 * HEIGHT,
                                                 0.505 * WIDTH, 0.14785992217898833 * HEIGHT));
        PATH1.getElements().add(new CubicCurveTo(0.5925 * WIDTH, 0.21011673151750973 * HEIGHT,
                                                 0.6725 * WIDTH, 0.3035019455252918 * HEIGHT,
                                                 0.7125 * WIDTH, 0.4085603112840467 * HEIGHT));
        PATH1.getElements().add(new ClosePath());
        //PATH1.getStyleClass().add("layer_1-path1");
        final Paint PATH1_FILL = Color.color(0.4, 0.6, 1, 1);
        PATH1.setFill(PATH1_FILL);
        PATH1.setStroke(null);

        logo.getChildren().addAll(PATH,
                                     PATH1);

        continueLogo(logo, WIDTH, HEIGHT);

    }

    private void continueLogo(Group logo, final double WIDTH, final double HEIGHT) {
        final Path PATH2 = new Path();
        PATH2.setFillRule(FillRule.EVEN_ODD);
        PATH2.getElements().add(new MoveTo(0.365 * WIDTH, 0.23346303501945526 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.32 * WIDTH, 0.2490272373540856 * HEIGHT,
                                                 0.325 * WIDTH, 0.33852140077821014 * HEIGHT,
                                                 0.28 * WIDTH, 0.3852140077821012 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.2925 * WIDTH, 0.35797665369649806 * HEIGHT,
                                                 0.3 * WIDTH, 0.3424124513618677 * HEIGHT,
                                                 0.3025 * WIDTH, 0.33852140077821014 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.305 * WIDTH, 0.32684824902723736 * HEIGHT,
                                                 0.3175 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                 0.32 * WIDTH, 0.2801556420233463 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.3225 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                 0.34 * WIDTH, 0.20622568093385213 * HEIGHT,
                                                 0.345 * WIDTH, 0.11284046692607004 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.3525 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                 0.34 * WIDTH, 0.21011673151750973 * HEIGHT,
                                                 0.365 * WIDTH, 0.23346303501945526 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        PATH2.getElements().add(new MoveTo(0.4125 * WIDTH, 0.2529182879377432 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.4025 * WIDTH, 0.25680933852140075 * HEIGHT,
                                                 0.3975 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                 0.3825 * WIDTH, 0.3035019455252918 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.37 * WIDTH, 0.33073929961089493 * HEIGHT,
                                                 0.3575 * WIDTH, 0.3463035019455253 * HEIGHT,
                                                 0.325 * WIDTH, 0.39299610894941633 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.375 * WIDTH, 0.3035019455252918 * HEIGHT,
                                                 0.38 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                 0.3925 * WIDTH, 0.2490272373540856 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.3975 * WIDTH, 0.22568093385214008 * HEIGHT,
                                                 0.4 * WIDTH, 0.20233463035019456 * HEIGHT,
                                                 0.4075 * WIDTH, 0.14007782101167315 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.41 * WIDTH, 0.1867704280155642 * HEIGHT,
                                                 0.3975 * WIDTH, 0.23346303501945526 * HEIGHT,
                                                 0.4125 * WIDTH, 0.2529182879377432 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        PATH2.getElements().add(new MoveTo(0.465 * WIDTH, 0.26848249027237353 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.445 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                 0.4125 * WIDTH, 0.3463035019455253 * HEIGHT,
                                                 0.3825 * WIDTH, 0.38910505836575876 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.415 * WIDTH, 0.3229571984435798 * HEIGHT,
                                                 0.42 * WIDTH, 0.3229571984435798 * HEIGHT,
                                                 0.44 * WIDTH, 0.2723735408560311 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.445 * WIDTH, 0.2490272373540856 * HEIGHT,
                                                 0.4525 * WIDTH, 0.23346303501945526 * HEIGHT,
                                                 0.45 * WIDTH, 0.17120622568093385 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.455 * WIDTH, 0.20622568093385213 * HEIGHT,
                                                 0.445 * WIDTH, 0.25680933852140075 * HEIGHT,
                                                 0.465 * WIDTH, 0.26848249027237353 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        PATH2.getElements().add(new MoveTo(0.5075 * WIDTH, 0.2801556420233463 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.495 * WIDTH, 0.2801556420233463 * HEIGHT,
                                                 0.4925 * WIDTH, 0.3035019455252918 * HEIGHT,
                                                 0.47 * WIDTH, 0.33852140077821014 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.4975 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                 0.4975 * WIDTH, 0.2529182879377432 * HEIGHT,
                                                 0.5025 * WIDTH, 0.21011673151750973 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.5 * WIDTH, 0.25680933852140075 * HEIGHT,
                                                 0.4975 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                 0.5075 * WIDTH, 0.2801556420233463 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        PATH2.getElements().add(new MoveTo(0.53 * WIDTH, 0.3346303501945525 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.545 * WIDTH, 0.29571984435797666 * HEIGHT,
                                                 0.5475 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                 0.55 * WIDTH, 0.245136186770428 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.5525 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                 0.5475 * WIDTH, 0.2840466926070039 * HEIGHT,
                                                 0.5525 * WIDTH, 0.29961089494163423 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.5425 * WIDTH, 0.29571984435797666 * HEIGHT,
                                                 0.555 * WIDTH, 0.29571984435797666 * HEIGHT,
                                                 0.53 * WIDTH, 0.3346303501945525 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        PATH2.getElements().add(new MoveTo(0.6 * WIDTH, 0.3229571984435798 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.595 * WIDTH, 0.3229571984435798 * HEIGHT,
                                                 0.59 * WIDTH, 0.33073929961089493 * HEIGHT,
                                                 0.5825 * WIDTH, 0.3424124513618677 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.595 * WIDTH, 0.3229571984435798 * HEIGHT,
                                                 0.5975 * WIDTH, 0.311284046692607 * HEIGHT,
                                                 0.6025 * WIDTH, 0.2918287937743191 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.6 * WIDTH, 0.311284046692607 * HEIGHT,
                                                 0.5975 * WIDTH, 0.311284046692607 * HEIGHT,
                                                 0.6 * WIDTH, 0.3229571984435798 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        PATH2.getElements().add(new MoveTo(0.6575 * WIDTH, 0.377431906614786 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.6525 * WIDTH, 0.377431906614786 * HEIGHT,
                                                 0.65 * WIDTH, 0.3735408560311284 * HEIGHT,
                                                 0.6375 * WIDTH, 0.38910505836575876 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.65 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                 0.655 * WIDTH, 0.36964980544747084 * HEIGHT,
                                                 0.655 * WIDTH, 0.35019455252918286 * HEIGHT));
        PATH2.getElements().add(new CubicCurveTo(0.6575 * WIDTH, 0.3735408560311284 * HEIGHT,
                                                 0.655 * WIDTH, 0.3735408560311284 * HEIGHT,
                                                 0.6575 * WIDTH, 0.377431906614786 * HEIGHT));
        PATH2.getElements().add(new ClosePath());
        //PATH2.getStyleClass().add("layer_1-path2");
        final Paint PATH2_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        PATH2.setFill(PATH2_FILL);
        PATH2.setStroke(null);

        final Path PATH3 = new Path();
        PATH3.setFillRule(FillRule.EVEN_ODD);
        PATH3.getElements().add(new MoveTo(0.78 * WIDTH, 0.6653696498054474 * HEIGHT));
        PATH3.getElements().add(new CubicCurveTo(0.7825 * WIDTH, 0.669260700389105 * HEIGHT,
                                                 0.7775 * WIDTH, 0.708171206225681 * HEIGHT,
                                                 0.77 * WIDTH, 0.7120622568093385 * HEIGHT));
        PATH3.getElements().add(new CubicCurveTo(0.7675 * WIDTH, 0.7159533073929961 * HEIGHT,
                                                 0.7525 * WIDTH, 0.7159533073929961 * HEIGHT,
                                                 0.7425 * WIDTH, 0.7159533073929961 * HEIGHT));
        PATH3.getElements().add(new CubicCurveTo(0.7375 * WIDTH, 0.7159533073929961 * HEIGHT,
                                                 0.7325 * WIDTH, 0.708171206225681 * HEIGHT,
                                                 0.73 * WIDTH, 0.7042801556420234 * HEIGHT));
        PATH3.getElements().add(new CubicCurveTo(0.7375 * WIDTH, 0.7042801556420234 * HEIGHT,
                                                 0.7525 * WIDTH, 0.7003891050583657 * HEIGHT,
                                                 0.755 * WIDTH, 0.6964980544747081 * HEIGHT));
        PATH3.getElements().add(new CubicCurveTo(0.765 * WIDTH, 0.688715953307393 * HEIGHT,
                                                 0.7725 * WIDTH, 0.6731517509727627 * HEIGHT,
                                                 0.78 * WIDTH, 0.6614785992217899 * HEIGHT));
        PATH3.getElements().add(new CubicCurveTo(0.78 * WIDTH, 0.6614785992217899 * HEIGHT,
                                                 0.78 * WIDTH, 0.6653696498054474 * HEIGHT,
                                                 0.78 * WIDTH, 0.6653696498054474 * HEIGHT));
        PATH3.getElements().add(new ClosePath());
        //PATH3.getStyleClass().add("layer_1-path3");
        final Paint PATH3_FILL = Color.color(0.4, 0.6, 1, 1);
        PATH3.setFill(PATH3_FILL);
        PATH3.setStroke(null);

        logo.getChildren().addAll(PATH2,
                                     PATH3);
    }

}

