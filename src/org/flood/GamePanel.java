package org.flood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The panel added into the Game.
 * <p/>
 * Created by Bernardo on 23/10/2014.
 */
class GamePanel extends JPanel {

    private static final int MAGIC_FONT_BORDER = 4;

    private int tileSide;
    private int tilesPerRow;
    private int totalTiles;

    private Theme theme;
    private TileMatrix tileMatrix;

    private int mouseClicks = 0;

    /**
     * The default constructor.
     */
    public GamePanel(GameSize gameSize) {
        super();
        tileMatrix = new TileMatrix(gameSize, GameData.DEFAULT_GENERATOR_MODE);
        setBackground(Color.BLACK);
        setTheme(GameData.THEMES[0]);
        resize(gameSize);
        // Set the font used to write the status.
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                int i = e.getX() / tileSide;
                int j = e.getY() / tileSide;
                if (i >= 0 && i < tilesPerRow && j >= 0 && j < tilesPerRow) {
                    mouseClicks++;
                    tileMatrix.startFlood(i, j);
                    repaint();
                    if (tileMatrix.getWaterCount() == totalTiles) {
                        endGameOptionPane();
                    }
                }
            }
        });
    }


    public void setTheme(Theme theme) {
        this.theme = theme;
        repaint();
    }

    /**
     * Updates all customizable variables.
     */
    void resize(GameSize gameSize) {
        tileMatrix = new TileMatrix(gameSize, GameData.DEFAULT_GENERATOR_MODE);
        tilesPerRow = gameSize.tilesPerRow;
        totalTiles = tilesPerRow * tilesPerRow;
        tileSide = gameSize.tileSide;
    }

    /**
     * Prompts the user (using a JOptionPane dialog) if he/she wants to play again or quit.
     */
    private void endGameOptionPane() {
        int choice = JOptionPane.showConfirmDialog(this, GameData.END_GAME_MESSAGE, GameData.END_GAME_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            reinitialize();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int j = 0; j < tilesPerRow; j++) {
            for (int i = 0; i < tilesPerRow; i++) {
                g.setColor(theme.colors.get(tileMatrix.getTileType(i, j)));
                g.fill3DRect(i * tileSide, j * tileSide, tileSide, tileSide, true);
            }
        }
        updateStatusBar(g);
    }

    /**
     * Reinitializes the game panel, erasing all the current progress made by the player. Used to update the panel
     * after a new panel size is set.
     */
    public void reinitialize() {
        tileMatrix.reinitialize();
        resetMouseClicks();
        repaint();
    }

    private void updateStatusBar(Graphics g) {
        g.setColor(Color.GREEN);
        int water = tileMatrix.getWaterCount();
        int total = totalTiles;
        StringBuilder stringBuilder = new StringBuilder();
        // Water over total tiles fraction.
        stringBuilder.append(Integer.toString(water)).append('/').append(Integer.toString(total)).append(' ');
        // Water percentage.
        stringBuilder.append('(').append(Utils.getPercentageString(water, totalTiles)).append(')');
        // Mouse click count.
        if (mouseClicks > 0) {
            stringBuilder.append(" after ");
            if (mouseClicks == 1) {
                stringBuilder.append("1 click");
            } else if (mouseClicks > 1) {
                stringBuilder.append(Integer.toString(mouseClicks)).append(" clicks");
            }
        }
        g.drawString(stringBuilder.toString(), MAGIC_FONT_BORDER, getHeight() - MAGIC_FONT_BORDER);
    }

    void resetMouseClicks() {
        mouseClicks = 0;
    }

}