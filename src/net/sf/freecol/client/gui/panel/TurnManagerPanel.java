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
 * This panel displays the Labour Report.
 */
public final class TurnManagerPanel extends ReportPanel {

    /** An individual unit type panel. */
    private static class ManagerPanel extends MigPanel {

        public boolean selected;
        public final UnitType unitType;

        public final Unit unit;


        public ManagerPanel(FreeColClient freeColClient, Unit unit,
                               int count) {
            super(new MigLayout("wrap 2", "[60, right][left]"));

            this.unit = unit;
            this.unitType = unit.getType();
            setOpaque(false);
            add(new JLabel(new ImageIcon(freeColClient.getGUI().getFixedImageLibrary()
                            .getSmallUnitTypeImage(unitType, (count == 1)))),
                    "spany 2");
            //add(new JLabel(Messages.getName(unitType)));
            add(new JLabel(unit.getDescription(Unit.UnitLabelType.NATIONAL)));
            add(new JLabel("Moves: " + unit.getMovesAsString()));


            if(unit.getState().getKey().equals("unitState.active"))
                add(new JLabel("SKIP"));
            else
                add(new JLabel("FREE"));
            setPreferredSize(getPreferredSize());
        }


        @Override
        public void paintComponent(Graphics g) {
            if (selected) {
                Graphics2D g2d = (Graphics2D) g;
                Composite oldComposite = g2d.getComposite();
                Color oldColor = g2d.getColor();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(oldComposite);
                g2d.setColor(oldColor);
            }
            super.paintComponent(g);
        }
    }

    /** A renderer for the labour unit panels. */
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


    /** The map of unit type to location and count. */
    private final java.util.Map<UnitType, java.util.Map<Location, Integer>> data;

    /** A map of count by unit type. */
    private final TypeCountMap<UnitType> unitCount;

    /** The player colonies. */
    private final List<Colony> colonies;

    /** A list of panels for the unit types. */
    private JList<ManagerPanel> panelList = null;

    private DefaultListModel<ManagerPanel> model;


    /**
     * The constructor that will add the items to this panel.
     *
     * @param freeColClient The enclosing {@code FreeColClient}.
     */
    public TurnManagerPanel(FreeColClient freeColClient) {
        super(freeColClient, "TurnManagerPanel");

        final Player player = getMyPlayer();
        this.data = new HashMap<>();
        this.unitCount = new TypeCountMap<>();
        for (Unit unit : player.getUnitSet()) {
            UnitType type = unit.getType();
            this.unitCount.incrementCount(type, 1);
            Map<Location, Integer> unitMap = this.data.get(type);
            if (unitMap == null) {
                unitMap = new HashMap<>();
                this.data.put(type, unitMap);
            }

            Location location = unit.getLocation();
            if (location == null) {
                logger.warning("Unit has null location: " + unit);
            } else if (location.getSettlement() != null) {
                location = location.getSettlement();
            } else if (unit.isInEurope()) {
                location = player.getEurope();
            } else if (location.getTile() != null) {
                location = location.getTile();
            }
            Integer count = unitMap.get(location);
            if (count == null) {
                unitMap.put(location, 1);
            } else {
                unitMap.put(location, count + 1);
            }
        }

        this.colonies = player.getColonyList();

        update(freeColClient);
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

    private void update(FreeColClient freeColClient) {
        reportPanel.removeAll();

        Player player = freeColClient.getMyPlayer();

        model = new DefaultListModel<>();
        for (Unit unit : player.getUnitSet()) {
            if (!unit.isInEurope() && !unit.isOnCarrier()) {
                model.addElement(new ManagerPanel(freeColClient, unit, 0));
            }
        }
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
        if (FreeColPanel.OK.equals(command)) {
            super.actionPerformed(ae);
        } else {
            UnitType unitType = getSpecification().getUnitType(command);
            getGUI().showReportLabourDetailPanel(unitType, this.data,
                    this.unitCount, this.colonies);
        }
    }
}
