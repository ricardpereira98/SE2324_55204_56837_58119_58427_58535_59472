/**
 * Copyright (C) 2002-2022   The FreeCol Team
 * <p>
 * This file is part of FreeCol.
 * <p>
 * FreeCol is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * FreeCol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.client.gui.panel.colopedia;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.action.ColopediaAction.PanelType;
import net.sf.freecol.client.gui.panel.FreeColPanel;
import net.sf.freecol.client.gui.panel.Utility;
import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.util.ImageUtils;


/**
 * This panel displays the concepts within the Colopedia.
 */
public class SeasonDetailPanel extends FreeColPanel
        implements ColopediaDetailPanel<String> {

    private static class SeasonEditorPane extends JEditorPane {

        /**
         * Build a new concept editor pane containing the given text.
         *
         * @param text The text to display.
         */
        public SeasonEditorPane(String text) {
            super("text/html", text);

            putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                    Boolean.TRUE);
            setOpaque(false);
            setEditable(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintComponent(Graphics g) {

            Graphics2D graphics2d = (Graphics2D) g;
            graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            super.paintComponent(graphics2d);
        }
    }

    ;

    private static final String id = "colopediaAction." + PanelType.SEASONS.getKey();
    private static final String WINTER = "winter";
    private static final String SPRING = "spring";
    private static final String SUMMER = "summer";
    private static final String AUTUMN = "autumn";

    private static final String[] seasons = {
            "winter",
            "spring",
            "summer",
            "autumn"

    };

    private static final Comparator<DefaultMutableTreeNode> nodeComparator =
            Comparator.comparing(tn -> ((ColopediaTreeItem) tn.getUserObject()).getText());

    private ColopediaPanel colopediaPanel;


    /**
     * Creates a new instance of this ColopediaDetailPanel.
     *
     * @param freeColClient The enclosing {@code FreeColClient}.
     * @param colopediaPanel The parent {@code ColopediaPanel}.
     */
    public SeasonDetailPanel(FreeColClient freeColClient,
                             ColopediaPanel colopediaPanel) {
        super(freeColClient);

        this.colopediaPanel = colopediaPanel;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.getName(id);
    }

    // Implement ColopediaDetailPanel

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubTrees(DefaultMutableTreeNode root) {

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ColopediaTreeItem(this, id, getName(), null));

        List<DefaultMutableTreeNode> nodes = new ArrayList<>(seasons.length);

        for (String season : seasons) {
            String nodeId = "colopedia.seasons." + season;
            String nodeName = Messages.getName(nodeId);
            BufferedImage image = null;

            switch (season) {
                case WINTER:
                    image = ImageUtils.createCenteredImage(getImageLibrary().getColopediaWinterImage(), colopediaPanel.getListItemIconSize());
                    break;
                case SPRING:
                    image = ImageUtils.createCenteredImage(getImageLibrary().getColopediaSpringImage(), colopediaPanel.getListItemIconSize());
                    break;
                case SUMMER:
                    image = ImageUtils.createCenteredImage(getImageLibrary().getColopediaSummerImage(), colopediaPanel.getListItemIconSize());
                    break;
                case AUTUMN:
                    image = ImageUtils.createCenteredImage(getImageLibrary().getColopediaAutumnImage(), colopediaPanel.getListItemIconSize());
                    break;
                default:
                    image = ImageUtils.createCenteredImage(getImageLibrary().getColopediaConceptImage(), colopediaPanel.getListItemIconSize());
                    break;
            }
            nodes.add(new DefaultMutableTreeNode(new ColopediaTreeItem(this, nodeId, nodeName, new ImageIcon(image))));
        }
        nodes.sort(nodeComparator);
        for (DefaultMutableTreeNode n : nodes) {
            node.add(n);
        }
        root.add(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildDetail(String id, JPanel panel) {
        if (SeasonDetailPanel.id.equals(id)) return;

        panel.setLayout(new MigLayout("wrap 1, center"));

        JLabel header = Utility.localizedHeaderLabel(Messages.nameKey(id), SwingConstants.LEADING, Utility.FONTSPEC_SUBTITLE);
        panel.add(header, "align center, wrap 20");

        JEditorPane editorPane = new SeasonEditorPane(Messages.getDescription(id));
        editorPane.setFont(panel.getFont());
        editorPane.addHyperlinkListener(colopediaPanel);
        panel.add(editorPane, "width 95%");
    }
}
