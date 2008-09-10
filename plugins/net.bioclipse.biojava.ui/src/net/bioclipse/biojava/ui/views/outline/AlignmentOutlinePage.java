package net.bioclipse.biojava.ui.views.outline;

import net.bioclipse.biojava.ui.editors.AlignmentEditor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class AlignmentOutlinePage extends ContentOutlinePage
                                  implements ISelectionListener, IAdaptable {

    private IEditorInput input;

    public AlignmentOutlinePage(IEditorInput input, AlignmentEditor editor) {
        super();

        this.input = input;
    }
    
    public void createControl(Composite parent) {
        final Canvas sequenceCanvas = new Canvas( parent, SWT.NONE );
        sequenceCanvas.setLocation( 0, 0 );
        sequenceCanvas.addPaintListener( new PaintListener() {
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                gc.fillRectangle(0, 0, 40, 40);
            }
        } );
    }

    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
    }

    public Object getAdapter( Class adapter ) {
        return null;
    }
    
}
