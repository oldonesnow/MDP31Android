package com.example.mdp31android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GridMap extends View {
    // Constants for grid dimensions
    private static final int COL = 15;
    private static final int ROW = 20;
    private static final int CELL_SIZE = 60; // The size of each grid cell

    private final Paint exploredPaint = new Paint();
    private final Paint unexploredPaint = new Paint();
    private final Paint gridLinePaint = new Paint();

    private int[][] cells; // 2D array for the grid

    public GridMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        initMap();
    }

    // Initialize paint objects for drawing the grid
    private void initPaints() {
        exploredPaint.setColor(Color.LTGRAY); // Color for explored cells
        unexploredPaint.setColor(Color.WHITE); // Color for unexplored cells
        gridLinePaint.setColor(Color.BLACK); // Color for grid lines
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(2);
    }

    // Initialize the grid with unexplored cells
    private void initMap() {
        cells = new int[ROW][COL];
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                cells[row][col] = 0; // 0 means unexplored
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the grid
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                // Determine which paint to use (explored or unexplored)
                if (cells[row][col] == 0) {
                    canvas.drawRect(col * CELL_SIZE, row * CELL_SIZE,
                            (col + 1) * CELL_SIZE, (row + 1) * CELL_SIZE, unexploredPaint);
                } else {
                    canvas.drawRect(col * CELL_SIZE, row * CELL_SIZE,
                            (col + 1) * CELL_SIZE, (row + 1) * CELL_SIZE, exploredPaint);
                }
                // Draw grid lines
                canvas.drawRect(col * CELL_SIZE, row * CELL_SIZE,
                        (col + 1) * CELL_SIZE, (row + 1) * CELL_SIZE, gridLinePaint);
            }
        }
    }

    // Optional: Handle touch events to mark cells as explored
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int col = (int) (event.getX() / CELL_SIZE);
        int row = (int) (event.getY() / CELL_SIZE);

        if (col >= 0 && col < COL && row >= 0 && row < ROW) {
            cells[row][col] = 1; // Mark the touched cell as explored
            invalidate(); // Redraw the grid
        }
        return true;
    }
}

