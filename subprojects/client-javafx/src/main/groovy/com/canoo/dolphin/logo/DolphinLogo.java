package com.canoo.dolphin.logo;

import javafx.scene.control.Control;


public class DolphinLogo extends Control {
    private static final String DEFAULT_STYLE_CLASS = "dolphinlogo";
    private boolean         square;
    private boolean         keepAspect;


    // ******************** Constructors **************************************
    public DolphinLogo() {
        square     = false;
        keepAspect = false;

        init();
    }

    private void init() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }


    // ******************** Methods *******************************************

    public final boolean isSquare() {
        return square;
    }

    public final boolean isKeepAspect() {
        return keepAspect;
    }

    @Override public void setPrefSize(final double WIDTH, final double HEIGHT) {
        double prefHeight = WIDTH < (HEIGHT * 1.5603112840466926) ? (WIDTH * 0.6408977556109726) : HEIGHT;
        double prefWidth = prefHeight * 1.5603112840466926;

        if (keepAspect) {
            super.setPrefSize(prefWidth, prefHeight);
        } else {
            super.setPrefSize(WIDTH, HEIGHT);
        }
    }


    // ******************** Style related *************************************
    @Override protected String getUserAgentStylesheet() {
        return getClass().getResource(getClass().getSimpleName().toLowerCase() + ".css").toExternalForm();
    }
}

