package net.sf.freecol.client.gui.panel;

        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.MouseAdapter;
        import java.awt.event.MouseEvent;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import javax.swing.AbstractAction;
        import javax.swing.Action;
        import javax.swing.DefaultListModel;
        import javax.swing.ImageIcon;
        import javax.swing.JLabel;
        import javax.swing.JList;
        import javax.swing.KeyStroke;
        import javax.swing.ListCellRenderer;
        import javax.swing.ListSelectionModel;
        import net.miginfocom.swing.MigLayout;
        import net.sf.freecol.FreeCol;
        import net.sf.freecol.client.FreeColClient;
        import net.sf.freecol.client.gui.panel.report.ReportPanel;
        import net.sf.freecol.common.i18n.Messages;
        import net.sf.freecol.common.model.Colony;
        import net.sf.freecol.common.model.Location;
        import net.sf.freecol.common.model.Player;
        import net.sf.freecol.common.model.TypeCountMap;
        import net.sf.freecol.common.model.Unit;
        import net.sf.freecol.common.model.UnitType;

        import static net.sf.freecol.common.model.Unit.UnitState.ACTIVE;
        import static net.sf.freecol.common.model.Unit.UnitState.SKIPPED;


/**
 * This panel displays the Turn Manager.
 */
public final class TurnManagerPanel extends ReportPanel {

    /** An individual unit panel. */
    private static class ManagerPanel extends MigPanel {

        public boolean selected;
        public final Unit unit;


        public ManagerPanel(FreeColClient freeColClient, Unit unit) {
            super(new MigLayout("wrap 2", "[60, right][left]"));

            this.unit = unit;

            setOpaque(false);
            add(new JLabel(new ImageIcon(freeColClient.getGUI().getFixedImageLibrary()
                            .getSmallUnitTypeImage(unit.getType(), false))),
                    "spany 2");

            add(new JLabel(unit.getDescription(Unit.UnitLabelType.NATIONAL)));
            add(new JLabel("Moves: " + unit.getMovesAsString()));

            if(unit.getState().getKey().equals("unitState.active"))
                add(new JLabel("SKIP UNIT"));
            else
                add(new JLabel("ACTIVATE UNIT"));

            setPreferredSize(getPreferredSize());
        }
    }

    /** A renderer for the unit panels. */
    private static class ManagerPanelRenderer
            implements ListCellRenderer<ManagerPanel> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getListCellRendererComponent(JList<? extends ManagerPanel> list,
                                                      ManagerPanel value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            value.selected = isSelected;
            return value;
        }
    }

    /** A list of panels for the player units. */
    private JList<ManagerPanel> panelList;

    /** A model based on the player units list. */
    private DefaultListModel<ManagerPanel> model;


    /**
     * The constructor that will add the items to this panel.
     *
     * @param freeColClient The enclosing {@code FreeColClient}.
     */
    public TurnManagerPanel(FreeColClient freeColClient) {
        super(freeColClient, "TurnManagerPanel");

        update(freeColClient);
    }


    /** The function that will handle the construction of the list. */
    private void update(FreeColClient freeColClient) {
        Player player = freeColClient.getMyPlayer();

        model = new DefaultListModel<>();
        for (Unit unit : player.getUnitSet()) {
            if (!unit.isInEurope() && !unit.isOnCarrier()) {
                model.addElement(new ManagerPanel(freeColClient, unit));
            }
        }

        Action selectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                toggleState(getFreeColClient() );
            }
        };
        Action quitAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                getGUI().removeComponent(TurnManagerPanel.this);
            }
        };

        // Add all the components
        this.panelList = new JList<>(model);
        this.panelList.getInputMap()
                .put(KeyStroke.getKeyStroke("ENTER"), "select");
        this.panelList.getActionMap().put("select", selectAction);
        this.panelList.getInputMap()
                .put(KeyStroke.getKeyStroke("ESCAPE"), "quit");
        this.panelList.getActionMap().put("quit", quitAction);
        this.panelList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleState(getFreeColClient());
            }
        });
        this.panelList.setOpaque(false);
        this.panelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.panelList.setLayoutOrientation(JList.VERTICAL);
        this.panelList.setCellRenderer(new ManagerPanelRenderer());

        this.scrollPane.setViewportView(this.panelList);
    }

    private void toggleState(FreeColClient freeColClient) {
        Unit unit = panelList.getSelectedValue().unit;

        if(unit.getState().getKey().equals("unitState.active"))
            unit.setState(Unit.UnitState.SKIPPED);
        else
            unit.setState(Unit.UnitState.ACTIVE);

        update(freeColClient);
    }


    // Interface ActionListener

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        final String command = ae.getActionCommand();
        if (FreeColPanel.OK.equals(command))
            super.actionPerformed(ae);
    }
}
