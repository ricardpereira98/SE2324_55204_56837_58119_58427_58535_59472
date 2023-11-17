package net.sf.freecol.client.gui.dialog;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ChoiceItem;
import net.sf.freecol.client.gui.panel.Utility;
import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.model.StringTemplate;

import javax.swing.*;
import java.util.List;

public class Tutorial extends FreeColConfirmDialog {

    public Tutorial (FreeColClient freeColClient, JFrame frame)
    {
        super(freeColClient, frame);
    }

    public Tutorial(FreeColClient freeColClient, JFrame frame, boolean modal, StringTemplate tmpl,
                                ImageIcon icon, String okKey, String cancelKey) {
        this(freeColClient, frame);

        initializeTutorial(frame, modal, Utility.localizedTextArea(tmpl),
                icon, okKey, cancelKey);
    }

    protected final void initializeTutorial(JFrame frame, boolean modal, JComponent jc, ImageIcon icon,
                                                 String okKey, String cancelKey) {
        List<ChoiceItem<Boolean>> c = choices();
        c.add(new ChoiceItem<>(Messages.message(okKey), Boolean.TRUE)
                .okOption());
        c.add(new ChoiceItem<>(Messages.message(cancelKey), Boolean.FALSE)
                .cancelOption().defaultOption());

        initializeDialog(frame, DialogType.QUESTION, modal, jc, icon, c);
    }
}
