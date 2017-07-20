package com.hm.eventossociales.util;

import com.hm.eventossociales.domain.Usuario;

/**
 * Created by hans6 on 04-06-2017.
 */

public interface MainActivityContract {
    public interface Presenter {
        void onShowData(Usuario usuario);
    }

    public interface View {
        void showData(Usuario usuario);
    }
}
