/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ui.controls.finder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.jkiss.dbeaver.Log;

/**
 * AdvancedListItem
 */
public class AdvancedListItem extends Canvas {

    private static final Log log = Log.getLog(AdvancedListItem.class);
    public static final int BORDER_MARGIN = 5;

    private final AdvancedList list;
    private String text;
    private final Image icon;
    private boolean isHover;

    public AdvancedListItem(AdvancedList list, String text, Image icon) {
        super(list.getContainer(), SWT.DOUBLE_BUFFERED);

        this.list = list;
        this.list.addItem(this);
        this.text = text;
        this.icon = icon;

        this.setBackground(list.getBackground());

        this.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                isHover = true;
                redraw();
            }

            @Override
            public void mouseExit(MouseEvent e) {
                isHover = false;
                redraw();
            }

            @Override
            public void mouseHover(MouseEvent e) {
                //redraw();
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                getList().notifyDefaultSelection();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                getList().setSelection(AdvancedListItem.this);
                setFocus();
            }
        });
        this.addPaintListener(this::painItem);
        this.addDisposeListener(e -> list.removeItem(this));

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getList().getSelectedItem() == null) {
                    getList().setSelection(AdvancedListItem.this);
                }
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.keyCode) {
                    case SWT.ARROW_LEFT:
                    case SWT.ARROW_RIGHT:
                    case SWT.ARROW_UP:
                    case SWT.ARROW_DOWN:
                    case SWT.CR:
                        if (getList().getSelectedItem() != null) {
                            getList().navigateByKey(e);
                        }
                        break;
                }
            }
        });

    }

    private void painItem(PaintEvent e) {
        Point itemSize = getSize();
        boolean isSelected = getList().getSelectedItem() == this;

        GC gc = e.gc;
        if (isSelected) {
            gc.setBackground(getList().getSelectionBackgroundColor());
            gc.setForeground(getList().getSelectionForegroundColor());
        } else if (isHover) {
            gc.setBackground(getList().getHoverBackgroundColor());
            gc.setForeground(getList().getForegroundColor());
        } else {
            gc.setBackground(getList().getBackgroundColor());
            gc.setForeground(getList().getForegroundColor());
        }

        if (isSelected || isHover) {
            gc.fillRoundRectangle(BORDER_MARGIN, BORDER_MARGIN, itemSize.x - BORDER_MARGIN * 2, itemSize.y - BORDER_MARGIN * 2, 5, 5);
        }

        Rectangle iconBounds = icon.getBounds();
        Point imageSize = getList().getImageSize();

        int imgPosX = (itemSize.x - imageSize.x) / 2;
        int imgPosY = BORDER_MARGIN;//(itemBounds.height - iconBounds.height) / 2 ;

        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(icon, 0, 0, iconBounds.width, iconBounds.height,
            imgPosX - e.x, imgPosY, imageSize.x, imageSize.y);

        Point textSize = gc.stringExtent(text);
        if (textSize.x > itemSize.x) textSize.x = itemSize.x;

        gc.drawText(text, (itemSize.x - textSize.x) / 2 - e.x, itemSize.y - textSize.y - BORDER_MARGIN * 2);
    }

    private AdvancedList getList() {
        return list;
    }

    public Image getIcon() {
        return icon;
    }

    public boolean isHover() {
        return isHover;
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point imageSize = getList().getImageSize();
        return new Point(imageSize.x + 30, imageSize.y + 30);
        //return super.computeSize(wHint, hHint, changed);//getList().getImageSize();
    }

    private Image resize(Image image, int width, int height) {
        Image scaled = new Image(getDisplay().getDefault(), width, height);
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0,  0,
            image.getBounds().width, image.getBounds().height,
            0, 0, width, height);
        gc.dispose();
        return scaled;
    }

}