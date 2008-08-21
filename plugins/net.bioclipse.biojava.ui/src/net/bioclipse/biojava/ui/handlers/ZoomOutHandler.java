package net.bioclipse.biojava.ui.handlers;

import net.bioclipse.biojava.ui.editors.AlignmentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


public class ZoomOutHandler extends AbstractHandler implements IHandler {

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        
        if (!(editor instanceof AlignmentEditor))
            return null;
        
        System.out.println("Zoom out!");
        
        return null;
    }

}
