package cmsc142.project.sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class GamePanelController implements ActionListener, KeyListener{
	GamePanel gamePanel;
	private List<SudokuBoard> sudokuBoards;
	private SudokuBoard currentBoard;
	private HashSet<Point> errorCells;
	int currentPuzzle=0;
	
	public GamePanelController(){
		this.gamePanel = new GamePanel();
		gamePanel.getBackMenuButton().addActionListener(this);
		
		FileAccess fileAccess = new FileAccess();
		try {
			sudokuBoards = fileAccess.readBoard("resources/input.in");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.errorCells = new HashSet<>(); 
		errorCells.add(new Point(1, 2));
//		
		if(sudokuBoards.size() > 0){
			currentBoard = sudokuBoards.get(currentPuzzle);	
			int puzzleSize = currentBoard.getPuzzleSize();
			drawTable(puzzleSize);
			
			if(sudokuBoards.size() > 1) gamePanel.getNextPuzzleButton().setEnabled(true);
		}
        
        gamePanel.getSudokuTable().addKeyListener(this);
        gamePanel.getNextPuzzleButton().addActionListener(this);
        gamePanel.getPrevPuzzleButton().addActionListener(this);
        gamePanel.validate();
        gamePanel.repaint();						
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == gamePanel.getNextPuzzleButton()){
			currentPuzzle++;
			currentBoard = sudokuBoards.get(currentPuzzle);
			drawTable(currentBoard.getPuzzleSize());
			
			if(currentPuzzle == sudokuBoards.size()-1) gamePanel.getNextPuzzleButton().setEnabled(false);
			if(currentPuzzle > 0) gamePanel.getPrevPuzzleButton().setEnabled(true);
		}
		
		if(event.getSource() == gamePanel.getPrevPuzzleButton()){	
			currentPuzzle--;
			currentBoard = sudokuBoards.get(currentPuzzle);
			drawTable(currentBoard.getPuzzleSize());
			
			if(currentPuzzle <= 0) gamePanel.getPrevPuzzleButton().setEnabled(false);
			if(currentPuzzle < sudokuBoards.size()-1) gamePanel.getNextPuzzleButton().setEnabled(true);
			
		}
	
	}
	
	public void drawTable(int puzzleSize){
		String data[][] = new String[puzzleSize][puzzleSize];
		String col[] = new String[puzzleSize];
		for(int i=0; i<puzzleSize; i++){
			for(int j=0; j<puzzleSize; j++){
				if(currentBoard.getPuzzle()[i][j] == 0){
					data[i][j] = "";
				}else{
					data[i][j] = String.valueOf(currentBoard.getPuzzle()[i][j]);
				}
			}
			col[i] = "";
		}
		
		DefaultTableModel model = new DefaultTableModel(data, col) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        gamePanel.getSudokuTable().setModel(model);
        gamePanel.getSudokuTable().setRowHeight((int) gamePanel.getSudokuTable().getPreferredSize().getWidth()/gamePanel.getSudokuTable().getRowCount());
        gamePanel.getSudokuTable().setCellSelectionEnabled(true);
        gamePanel.getSudokuTable().setColumnSelectionAllowed(false);
        gamePanel.getSudokuTable().setRowSelectionAllowed(false);
        CellRender cellRenderer = new CellRender();   // See below
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        gamePanel.getSudokuTable().setDefaultRenderer(Object.class, cellRenderer);
	}

	public GamePanel getGamePanel() {
		return gamePanel;
	}

	public void setGamePanel(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public class CellRender extends DefaultTableCellRenderer  { 
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean   isSelected, boolean hasFocus, int row, int column){ 
		    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
		    errorCells.add(new Point(table.getSelectedRow(), table.getSelectedColumn()));
		    if(errorCells.contains(new Point(row, column))){
		    	c.setBackground(new Color(210, 0, 0));
		    } else if(((row/(int)Math.sqrt(currentBoard.getPuzzleSize()))%2==0 && (column/(int)Math.sqrt(currentBoard.getPuzzleSize()))%2==0) ||
		    		((row/(int)Math.sqrt(currentBoard.getPuzzleSize()))%2==1 && (column/(int)Math.sqrt(currentBoard.getPuzzleSize()))%2==1)){
		        c.setBackground(new Color(210, 210, 210)); 
		    } else {
		    	c.setBackground(new Color(240,240,240));
		    }
		    return c; 
		}

	}

}
