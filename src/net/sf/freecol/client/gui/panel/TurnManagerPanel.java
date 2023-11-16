package net.sf.freecol.client.gui.panel;

import java.util.List;
import java.util.logging.Logger;

import net.miginfocom.swing.MigLayout;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.*;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Player;

import javax.swing.*;

public final class TurnManagerPanel extends FreeColPanel {

    @SuppressWarnings("unused")
    //private static final Logger logger = Logger.getLogger(TurnManagerPanel.class.getName());

    public TurnManagerPanel(FreeColClient freeColClient) {
        super(freeColClient, null, new MigLayout("wrap 1", "", ""));
        //update();
    }

    public void update() {

        //final Player player = getMyPlayer();
        //final Europe europe = player.getEurope();
    }

}
