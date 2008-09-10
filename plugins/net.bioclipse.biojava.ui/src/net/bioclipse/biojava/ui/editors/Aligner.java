/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.biojava.ui.editors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.ui.editors.ColorManager;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojavax.bio.seq.RichSequence.IOTools;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class Aligner extends EditorPart {

    private int squareSize = 20;
    private final static int MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS = 8,
                             NAME_CANVAS_WIDTH_IN_SQUARES = 8;

    static final Display display = Display.getCurrent();
    static final ColorManager colorManager = new ColorManager();
    
    static private final Color
        normalAAColor   = display.getSystemColor( SWT.COLOR_WHITE ),
        polarAAColor    = colorManager.getColor( new RGB(0xD0, 0xFF, 0xD0) ),
        nonpolarAAColor = colorManager.getColor( new RGB(0xFF, 0xFF, 0xD0) ),
        acidicAAColor   = colorManager.getColor( new RGB(0xFF, 0xD0, 0xA0) ),
        basicAAColor    = colorManager.getColor( new RGB(0xD0, 0xFF, 0xFF) ),
        smallAAColor    = colorManager.getColor( new RGB(0xFF, 0xD0, 0xD0) ),
        cysteineColor   = colorManager.getColor( new RGB(0xFF, 0xFF, 0xD0) ),
        textColor       = display.getSystemColor( SWT.COLOR_BLACK ),
        nameColor       = display.getSystemColor( SWT.COLOR_WHITE ),
        buttonColor     = colorManager.getColor( new RGB(0x66, 0x66, 0x66) ),
        consensusColor  = colorManager.getColor( new RGB(0xAA, 0xAA, 0xAA) ),
        selectionColor1 = display.getSystemColor( SWT.COLOR_BLACK ),
        selectionColor2 = display.getSystemColor( SWT.COLOR_BLACK );
    
    static private final Color[] consensusColors
        = generateColorList( new int[] {
                0xFFFFDD, // only one type; total consensus
                0xEEEEBE, // two different types
                0xDDDDB0, // three...
                0xCCCCA3, // ...
                0xBBBB95,
                0xAAAA88,
                0x99997A,
                0x88886C,
                0x77775F
        } );
    
    //          seqname, sequence
    private Map<String,  String> sequences;
    
    private int canvasWidthInSquares, canvasHeightInSquares;

    private int consensusRow;

    private Point selectionStart                = new Point(0, 0),
                  selectionEnd                  = new Point(0, 0),
                  dragStart                     = new Point(0, 0),
                  dragEnd                       = new Point(0, 0),
                  selectionTopLeftInSquares     = new Point(0, 0),
                  selectionBottomRightInSquares = new Point(0, 0);
    
    private boolean currentlySelecting         = false,
                    currentlyDraggingSelection = false,
                    selectionVisible           = false;

    private GridData data;
    private Composite parent;
    private Composite c;
    
    @Override
    public void doSave( IProgressMonitor monitor ) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init( IEditorSite site, IEditorInput input )
        throws PartInitException {
        
        if (!(input instanceof IFileEditorInput))
            throw new PartInitException(
                "Invalid Input: Must be IFileEditorInput");
        
        setSite(site);
        setInput(input);
    }
    
    @Override
    public void setInput( IEditorInput input ) {
        super.setInput(input);
        
        sequences = new LinkedHashMap<String, String>();

        // Turn the editor input into an IFile.
        IFile file = (IFile) input.getAdapter( IFile.class );
        if (file == null)
            return;

        SequenceIterator iter;
        try {
            // Create a BufferedInputStream for our IFile.
            BufferedReader br
                = new BufferedReader(new InputStreamReader(file.getContents()));
            
            // Create an iterator from the BufferedInputStream.
            // We have to generalize this from just proteins to anything.
            // The 'null' indicates that we don't care about which
            // namespace the sequence ends up getting.
            iter = IOTools.readFastaProtein( br, null );
        } catch ( CoreException ce ) {
            // File not found. TODO: This should be logged.
            ce.printStackTrace();
            return;
        }

        try {
            // Add the sequences one by one to the Map. Do minor cosmetics
            // on the name by removing everything up to and including to
            // the last '|', if any.
            while ( iter.hasNext() ) {
                Sequence s = iter.nextSequence();
                String name = s.getName().replaceFirst( ".*\\|", "" );
                sequences.put( name, s.seqString() );
            }
        }
        catch ( BioException e ) {
            // There was a parsing error. TODO: This should be logged.
            e.printStackTrace();
        }

        // We only show a consensus sequence if there is more than one
        // sequence already.
        consensusRow  = sequences.size();
        if (consensusRow > 1) {
            sequences.put(
                "Consensus",
                consensusSequence( sequences.values() )
            );
        }
        
        canvasHeightInSquares = sequences.size();
        canvasWidthInSquares = maxLength( sequences.values() );
    }

    private static String consensusSequence( final Collection<String>
                                                   sequences ) {

        final StringBuilder consensus = new StringBuilder();
        for ( int i = 0, n = maxLength(sequences); i < n; ++i ) {
            consensus.append( consensusChar(sequences, i) );
        }
        
        return consensus.toString();
    }
    
    private static int maxLength( final Collection<String> strings ) {
        
        int maxLength = 0;
        for ( String s : strings )
            if ( maxLength < s.length() )
                maxLength = s.length();
        
        return maxLength;
    }
    
    private static char consensusChar( final Collection<String> sequences,
                                       final int index ) {
        
        Map<Character, Boolean> columnChars
            = new HashMap<Character, Boolean>();
        
        for ( String seq : sequences )
            columnChars.put( seq.length() > index
                               ? seq.charAt(index)
                               : '\0',
                             true );
        
        return columnChars.size() == 1
               ? columnChars.keySet().iterator().next()
               : Character.forDigit( Math.min(columnChars.size(), 9), 10 );
    }

    static private Color[] generateColorList( int[] rgbList ) {
        List<Color> colors = new ArrayList<Color>();
        for ( int rgb : rgbList ) {
            colors.add( colorManager.getColor( new RGB( (rgb >> 16) & 0xFF,
                                                        (rgb >>  8) & 0xFF,
                                                        (rgb >>  0) & 0xFF )));
        }
        return colors.toArray(new Color[0]);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    private void selectionBounds() {
        int xLeft   = Math.min( selectionStart.x, selectionEnd.x ),
            xRight  = Math.max( selectionStart.x, selectionEnd.x ),
            yTop    = Math.min( selectionStart.y, selectionEnd.y ),
            yBottom = Math.max( selectionStart.y, selectionEnd.y );
    
        // clip
        xLeft   = Math.max( xLeft, 0 );
        xRight  = Math.min( xRight, canvasWidthInSquares * squareSize );
        yTop    = Math.max( yTop, 0 );
        yBottom = Math.min( yBottom, (canvasHeightInSquares-1) * squareSize );
    
        // round down
        xLeft  =                   xLeft / squareSize;
        yTop   =                    yTop / squareSize;
    
        // round up
        xRight  =  (xRight+squareSize-1) / squareSize;
        yBottom = (yBottom+squareSize-1) / squareSize;
    
        // make sure a selection always has positive area
        if ( xRight <= xLeft )
            xRight = xLeft + 1;
        if ( yBottom <= yTop
             && yTop < canvasHeightInSquares-1 )
            yBottom = yTop + 1;
    
        // special case: mark along the consensus row
        if ( yTop == yBottom )
            yTop = 0;

        selectionTopLeftInSquares.x = xLeft;
        selectionTopLeftInSquares.y = yTop;
        selectionBottomRightInSquares.x = xRight;
        selectionBottomRightInSquares.y = yBottom;
    }
    
    @Override
    public void createPartControl( Composite parent ) {
        
        this.parent = parent;
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        parent.setLayout( layout );
        
        final Canvas nameCanvas = new Canvas( parent, SWT.NONE );
        data = new GridData(GridData.FILL_VERTICAL);
        nameCanvas.setLayoutData( data );
        
        nameCanvas.addPaintListener( new PaintListener() {
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                gc.setBackground( buttonColor );
                if (squareSize >= MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS) {
                    gc.setFont( new Font(gc.getDevice(),
                                         "Arial",
                                         (int)(.7 * squareSize),
                                         SWT.NONE) );
                    gc.setForeground( nameColor );
                    gc.setTextAntialias( SWT.ON );
                }

                int index = 0;
                for ( String name : sequences.keySet() ) {
                    if ( index == consensusRow )
                        gc.setBackground( consensusColor );
                    gc.fillRectangle(0, index * squareSize,
                                    8 * squareSize, squareSize);
                    if (squareSize >= MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS)
                        gc.drawText( name, 5, index * squareSize + 2 );
                    ++index;
                }
            }
        });
        
        final ScrolledComposite sc
            = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        GridData sc_data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                                        | GridData.FILL_BOTH);
        sc.setLayoutData( sc_data );
        
        c = new Composite(sc, SWT.NONE);
        c.setLayout( new FillLayout() );
        
        final Canvas sequenceCanvas = new Canvas( c, SWT.NONE );
        sequenceCanvas.setLocation( 0, 0 );
        setCanvasSizes();
        sc.setContent( c );
        
        final char fasta[][] = new char[ sequences.size() ][];

        {
            int i = 0;
            for ( String sequence : sequences.values() )
                fasta[i++] = sequence.toCharArray();
        }
        
        sequenceCanvas.addPaintListener( new PaintListener() {
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                if ( squareSize >= MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS ) {
                    gc.setTextAntialias( SWT.ON );
                    gc.setFont( new Font(gc.getDevice(),
                                         "Arial",
                                         (int)(.7 * squareSize),
                                         SWT.NONE) );
                    gc.setForeground( textColor );
                }

                int firstVisibleColumn
                        = sc.getHorizontalBar().getSelection() / squareSize,
                    lastVisibleColumn
                        = firstVisibleColumn
                          + sc.getBounds().width / squareSize
                          + 2; // compensate for 2 possible round-downs
                
                drawSequences(fasta, firstVisibleColumn, lastVisibleColumn, gc);
                drawSelection( gc );
                drawConsensusSequence(
                    fasta[canvasHeightInSquares-1],
                    firstVisibleColumn, lastVisibleColumn, gc);
            }

            private void drawSequences( final char[][] fasta,
                                        int firstVisibleColumn,
                                        int lastVisibleColumn, GC gc ) {

                for ( int column = firstVisibleColumn;
                      column < lastVisibleColumn; ++column ) {

                    int xCoord = column * squareSize;

                    for ( int row = 0; row < canvasHeightInSquares-1; ++row ) {
                        
                        char c = fasta[row].length > column
                                 ? fasta[row][column] : ' ';
                        String cc = c + "";

                        gc.setBackground(
                             "HKR".contains( cc ) ? basicAAColor
                          :   "DE".contains( cc ) ? acidicAAColor
                          : "TQSN".contains( cc ) ? polarAAColor
                          :  "FYW".contains( cc ) ? nonpolarAAColor
                          :   "GP".contains( cc ) ? smallAAColor
                          :    'C' == c           ? cysteineColor
                                                  : normalAAColor );
                        
                        int yCoord = row * squareSize;
                        
                        gc.fillRectangle(xCoord, yCoord,
                                         squareSize, squareSize);
                        
                        if ( Character.isUpperCase( c )
                             && squareSize
                                  >= MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS )
                            gc.drawText( "" + c, xCoord + 4, yCoord + 2 );
                    }
                }
            }
            
            private void drawConsensusSequence( final char[] sequence,
                                                int firstVisibleColumn,
                                                int lastVisibleColumn, GC gc ) {

                int yCoord = (canvasHeightInSquares-1) * squareSize;
                
                for ( int column = firstVisibleColumn;
                      column < lastVisibleColumn; ++column ) {

                    char c = sequence.length > column ? sequence[column] : ' ';
                    int consensusDegree = Character.isDigit(c) ? c - '0' : 1;
                            
                    gc.setBackground(consensusColors[ consensusDegree-1 ]);
                        
                    int xCoord = column * squareSize;
                        
                    gc.fillRectangle(xCoord, yCoord, squareSize, squareSize);
                        
                    if ( Character.isUpperCase( c )
                         && squareSize
                              >= MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS )
                        gc.drawText( "" + c, xCoord + 4, yCoord + 2 );
                }
            }
            
            private void drawSelection( GC gc ) {
                
                if (!selectionVisible)
                    return;

                int dragXDistance = dragEnd.x - dragStart.x,
                    dragYDistance = dragEnd.y - dragStart.y,
                    xLeft
                      = selectionTopLeftInSquares.x * squareSize + dragXDistance,
                    yTop
                      = selectionTopLeftInSquares.y * squareSize + dragYDistance,
                    xRight
                      = selectionBottomRightInSquares.x   * squareSize + dragXDistance,
                    yBottom
                      = selectionBottomRightInSquares.y   * squareSize + dragYDistance;
                
                gc.setForeground( selectionColor1 );
                gc.drawRectangle( xLeft,
                                  yTop,
                                  xRight - xLeft - 1,
                                  yBottom - yTop - 1 );
                
                gc.setBackground( selectionColor2 );
                gc.setAlpha( 64 ); // 25%
                gc.fillRectangle( xLeft           + 1,
                                  yTop            + 1,
                                  xRight - xLeft  - 1,
                                  yBottom - yTop  - 1 );
                gc.setAlpha( 255 ); // opaque again
            }
        });
        
        sequenceCanvas.addMouseListener( new MouseListener() {

            public void mouseDoubleClick( MouseEvent e ) {
                // we're not interested in double clicks
            }

            public void mouseDown( MouseEvent e ) {
                if (e.button != 1)
                    return;

                int dragXDistance = dragEnd.x - dragStart.x,
                    dragYDistance = dragEnd.y - dragStart.y,
                    xLeft
                      = selectionTopLeftInSquares.x * squareSize + dragXDistance,
                    yTop
                      = selectionTopLeftInSquares.y * squareSize + dragYDistance,
                    xRight
                      = selectionBottomRightInSquares.x   * squareSize + dragXDistance,
                    yBottom
                      = selectionBottomRightInSquares.y   * squareSize + dragYDistance;
                
                if ( selectionVisible
                     && xLeft <= e.x && e.x <= xRight
                     && yTop  <= e.y && e.y <= yBottom ) {
                    
                    currentlyDraggingSelection = true;
                    dragStart.x = dragEnd.x = e.x;
                    dragStart.y = dragEnd.y = e.y;
                }
                else {
                    currentlySelecting = true;
                    selectionVisible = false;
                    selectionStart.x = selectionEnd.x = e.x;
                    selectionStart.y = selectionEnd.y = e.y;
                    sequenceCanvas.redraw();
                }
            }

            public void mouseUp( MouseEvent e ) {
                
                if (currentlyDraggingSelection) {
                    // The expressions do three things:
                    //
                    // 1. Calculate the distance dragged (end minus start)
                    // 2. Add half a square in that direction
                    // 3. Round towards zero to the closest squareSize point
                    //
                    // The second step is required precisely because the third
                    // step rounds towards zero.
                    int xDelta
                          = (dragEnd.x - dragStart.x                       // 1
                             + squareSize/2 * (dragEnd.x<dragStart.x?-1:1) // 2
                            ) / squareSize,                                // 3
                        yDelta
                          = (dragEnd.y - dragStart.y                       // 1
                             + squareSize/2 * (dragEnd.y<dragStart.y?-1:1) // 2
                            ) / squareSize;                                // 3
                    
                    selectionTopLeftInSquares.x += xDelta;
                    selectionBottomRightInSquares.x   += xDelta;

                    selectionTopLeftInSquares.y += yDelta;
                    selectionBottomRightInSquares.y   += yDelta;
                    
                    sequenceCanvas.redraw();
                }
                
                dragEnd = new Point(dragStart.x, dragStart.y);
                currentlySelecting = currentlyDraggingSelection = false;
            }
            
        });
        
        sequenceCanvas.addMouseMoveListener( new MouseMoveListener() {

            public void mouseMove( MouseEvent e ) {

                // e.stateMask contains info on shift keys
                if (currentlySelecting) {
                  selectionEnd.x = e.x;
                  selectionEnd.y = e.y;
                  
                  selectionVisible = true;

                  int viewPortLeft  = -c.getLocation().x,
                      viewPortRight = viewPortLeft + sc.getBounds().width,
                      viewPortTop   = -c.getLocation().y,
                      maximumLeft   = sc.getHorizontalBar().getMaximum();
                  
                  if ( e.x > viewPortRight ) {
                      viewPortLeft += e.x - viewPortRight;
                      if (viewPortRight >= maximumLeft )
                          viewPortLeft = maximumLeft - sc.getBounds().width;
                  }
                  else if ( e.x < viewPortLeft ) {
                      viewPortLeft -= viewPortLeft - e.x;
                      if (viewPortLeft < 0)
                          viewPortLeft = 0;
                  }
                  
                  if ( viewPortLeft != -c.getLocation().x ) {
                      sc.getHorizontalBar().setSelection( viewPortLeft );
                      c.setLocation( -viewPortLeft, -viewPortTop );
                  }
                  
                  sequenceCanvas.redraw();
                  
                  selectionBounds();
                }
                
                if (currentlyDraggingSelection) {
                    dragEnd.x = e.x;
                    dragEnd.y = e.y;
                    
                    sequenceCanvas.redraw();
                }
            }
            
        });
    }

    @Override
    public void setFocus() {
    }

    private void setCanvasSizes() {

        data.widthHint = squareSize >= MINIMUM_SQUARE_SIZE_FOR_TEXT_IN_PIXELS
                         ? NAME_CANVAS_WIDTH_IN_SQUARES * squareSize : 0;
        c.setSize( canvasWidthInSquares * squareSize,
                   canvasHeightInSquares * squareSize );
    }

    public void zoomIn() {
        squareSize++;
        setCanvasSizes();
        parent.layout();
        parent.redraw();
    }

    public void zoomOut() {
        if ( squareSize > 1 )
            squareSize--;
        setCanvasSizes();
        parent.layout();
        parent.redraw();
    }
}
