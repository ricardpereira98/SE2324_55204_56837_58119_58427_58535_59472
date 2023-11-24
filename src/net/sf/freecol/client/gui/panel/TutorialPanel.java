package net.sf.freecol.client.gui.panel;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.FontLibrary;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.model.*;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.resources.ImageResource;
import net.sf.freecol.common.resources.ResourceManager;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class TutorialPanel extends FreeColPanel{

    /** The skin for this panel. */
    private BufferedImage skin = null;
    private BufferedImage im = null;


    /**
     * Creates an information panel that shows the given
     * texts and images, and an "OK" button.
     */
    public TutorialPanel(FreeColClient freeColClient, String[] texts,
                            FreeColObject[] fcos, ImageIcon[] images, String []imkey) {
        super(freeColClient, null, new MigLayout());

        final ImageLibrary fixedImageLibrary = freeColClient.getGUI().getFixedImageLibrary();
        this.skin = fixedImageLibrary.getInformationPanelSkin(freeColClient.getMyPlayer());

        //Header Tutorial
        add(Utility.localizedHeader("Tutorial",Utility.FONTSPEC_SUBTITLE), "span,  align center, gaptop -100, gapbottom 0");


        final float scaleFactor = fixedImageLibrary.getScaleFactor();
        final int topInset = fixedImageLibrary.getInformationPanelSkinTopInset(freeColClient.getMyPlayer());
        final int scaledTopInset = (int) (topInset * scaleFactor);
        final int gap = 0;

        getMigLayout().setLayoutConstraints("fill, wrap 1, insets 0 0 0 0");
        getMigLayout().setColumnConstraints(gap + "[grow]" + gap);
        getMigLayout().setRowConstraints(scaledTopInset + "px[grow]" + gap + "[]" + gap);

        final JPanel textPanel = createPanelWithAllContent(texts, /* fcos, images,*/ gap, imkey);
        final JScrollPane scrollPane = new JScrollPane(textPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, "grow");
        add(okButton, "tag ok");
        setPreferredSize(new Dimension(skin.getWidth(), skin.getHeight()));
        setBorder(null);

        setEscapeAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                okButton.doClick();
            }
        });
    }

    private JPanel createPanelWithAllContent(String[] texts,/* FreeColObject[] fcos, ImageIcon[] images,*/ final int gap, String [] imkey) {
        JPanel textPanel = new MigPanel(new MigLayout("fill, wrap 2"));
        textPanel.setOpaque(false);


        for (int i = 0; i < texts.length; i++) {

            JTextArea txt = Utility.getDefaultTextArea(texts[i]);

            //gets the image
            if(imkey [i] != null) {
                BufferedImage hm = ResourceManager.getImage(imkey[i]);
                ImageIcon im = new ImageIcon(hm);

                if (im != null) {
                    //adds the image to the panel
                    textPanel.add(new JLabel(im));

                    //adds text to the panel
                    textPanel.add(txt, "gapleft " + gap + ", growx");
                }
            }
            else {
                textPanel.add(txt, "skip, growx");
            }


            /*if (images [i] != null) {
                textPanel.add(new JLabel(images [i]));
                textPanel.add(txt, "gapleft " + gap + ", growx");
            } else {
                textPanel.add(txt, "skip, growx");
            }
           StringTemplate disp = displayLabel(fcos[i]);
            if (disp != null) {
                JButton button = Utility.localizedButton(StringTemplate
                        .template("informationPanel.display")
                        .addStringTemplate("%object%", disp));
                final FreeColObject fco = fcos[i];
                button.addActionListener((ActionEvent ae) -> {
                    getGUI().displayObject(fco);
                });
                /*
                  If there is another text to display, we need to add
                  "gapbottom 25" into the .add(), which gives some
                  cushion between each text block

                if ((i + 1) < texts.length) {
                    textPanel.add(button, "skip, gapbottom 25");
                } else {
                    textPanel.add(button, "skip");
                }
            }*/
        }
        return textPanel;
    }

    /**
     * A label for an FCO that can meaningfully be displayed.
     *
     * @param fco The {@code FreeColObject} to check.
     * @return A {@code StringTemplate} label, or null if nothing found.
     */
    private StringTemplate displayLabel(FreeColObject fco) {
        return (fco instanceof Tile && ((Tile)fco).hasSettlement())
                ? displayLabel(((Tile)fco).getSettlement())

                : (fco instanceof Unit)
                ? displayLabel((FreeColObject)((Unit)fco).getLocation())

                : (fco instanceof Location)
                ? ((Location)fco).getLocationLabelFor(getMyPlayer())

                : null;


    }



    // Override JComponent

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(this.skin, 0, 0, this);
    }

}
