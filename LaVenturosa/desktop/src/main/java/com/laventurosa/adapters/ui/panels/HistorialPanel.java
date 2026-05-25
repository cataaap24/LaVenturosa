package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.usecases.services.VenturosaApp;

public class HistorialPanel implements AppAware {

    private VenturosaApp app;

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
    }
}
