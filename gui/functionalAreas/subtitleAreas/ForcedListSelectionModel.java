package gui.functionalAreas.subtitleAreas;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;


// FROM : http://stackoverflow.com/questions/18309113/jtable-how-to-force-user-to-select-exactly-one-row
public class ForcedListSelectionModel extends DefaultListSelectionModel {

    public ForcedListSelectionModel () {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
    }
}
