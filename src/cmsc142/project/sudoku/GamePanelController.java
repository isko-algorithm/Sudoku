package cmsc142.project.sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class GamePanelController implements ActionListener, KeyListener{
	GamePanel gamePanel;
	private List<SudokuBoard> sudokuBoards;
	private SudokuBoard currentBoard;
	private HashSet<Point> errorCells;
	int currentPuzzle=0;
	int currentSolutionPointer = 0;
	List<int[][]> solution;
	String currentType;
	int tickCount = 0;
	private boolean isSpecialSudokuActivated;
	private int[][] currentStateOfTable;
	public GamePanelController(){
		this.gamePanel = new GamePanel();
		this.errorCells = new HashSet<>(); 
		Timer timer = new Timer(1000, this);
		gamePanel.setTimer(timer);
		
		gamePanel.getBackMenuButton().addActionListener(this);
		currentType = (String) gamePanel.getTypeComboBox().getSelectedItem();
		gamePanel.getTypeComboBox().addActionListener(this);
		
		gamePanel.getActivateSpecialButton().addActionListener(this);
        gamePanel.getSudokuTable().addKeyListener(this);
        gamePanel.getSolverButton().addActionListener(this);
        gamePanel.getNextPuzzleButton().addActionListener(this);
        gamePanel.getPrevPuzzleButton().addActionListener(this);
        gamePanel.getNextSolutionButton().addActionListener(this);
        gamePanel.getPrevSolutionButton().addActionListener(this);
        gamePanel.getCheckerButton().addActionListener(this);
        gamePanel.getResetButton().addActionListener(this);
        gamePanel.validate();
        gamePanel.repaint();						
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(gamePanel.getSolverButton())){
			String[] options = { "Solve the current state.", "Solve the original state."};    
			int response = JOptionPane.showOptionDialog(gamePanel, "Do you want to solve the puzzle using the current state or the original state?",  "Solve Puzzle Confirmation",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			
			boolean xSudoku = false;
			boolean ySudoku = false;

			switch (currentType) {
				case "X":
					xSudoku = true;
					ySudoku = false;
					break;
	
				case "Y":
					xSudoku = false;
					ySudoku = true;
					break;
				
				case "XY":
					xSudoku = true;
					ySudoku = true;
					break;
			}
			
			if(response == JOptionPane.OK_OPTION || response == JOptionPane.NO_OPTION){
				SudokuBoard board = currentBoard;
				
				if(response == JOptionPane.YES_OPTION){
					board = new SudokuBoard(currentBoard.getPuzzleSize(), currentStateOfTable);
					SudokuUtils.solveUsingBacktracking(board, xSudoku, ySudoku);
				}
				 
				solution =  null;
				if(xSudoku){
					if(ySudoku){
						solution = board.getxYSolution();
					} else {
						solution = board.getxSolution();
					}
				} else {
					if(ySudoku){
						solution = board.getySolution();
					} else {
						solution = board.getNormalSolution();
					}
				}				
				
				if(solution.size() == 0){
					JOptionPane.showMessageDialog(gamePanel, new JLabel("There is no solution for the puzzle's current state."));
				}
				
				currentSolutionPointer = 0;
				drawTable(solution.get(currentSolutionPointer));
				if(solution.size() > 1)
					gamePanel.getNextSolutionButton().setEnabled(true);
				gamePanel.getTimer().stop();
			}
			
		} else if(event.getSource().equals(gamePanel.getNextSolutionButton())){
			currentSolutionPointer++;
			drawTable(solution.get(currentSolutionPointer));
			if(currentSolutionPointer+1 == solution.size())
				gamePanel.getNextSolutionButton().setEnabled(false);
			gamePanel.getPrevSolutionButton().setEnabled(true);
				
		} else if(event.getSource().equals(gamePanel.getPrevSolutionButton())){
			currentSolutionPointer--;
			drawTable(solution.get(currentSolutionPointer));
			if(currentSolutionPointer == 0)
				gamePanel.getPrevSolutionButton().setEnabled(false);
			gamePanel.getNextSolutionButton().setEnabled(true);
				
		} else if(event.getSource().equals(gamePanel.getNextPuzzleButton())){
			int response = JOptionPane.showConfirmDialog(gamePanel, new JLabel("Previous changes will not be saved. Do you really want to proceed to the next puzzle?"), "Warning!", JOptionPane.OK_CANCEL_OPTION);
			if(response == JOptionPane.OK_OPTION){
				currentPuzzle++;
				currentBoard = sudokuBoards.get(currentPuzzle);
				isSpecialSudokuActivated = false;
				currentStateOfTable = SudokuUtils.copyPuzzle(currentBoard.getPuzzle());
				resetPuzzle();
				currentType = gamePanel.getTypeComboBox().getItemAt(0).toString();
				gamePanel.getTypeComboBox().setSelectedItem(currentType);

				if(currentPuzzle == sudokuBoards.size()-1) gamePanel.getNextPuzzleButton().setEnabled(false);
				if(currentPuzzle > 0) gamePanel.getPrevPuzzleButton().setEnabled(true);
			}
		} else if(event.getSource() == gamePanel.getPrevPuzzleButton()){	
			int response = JOptionPane.showConfirmDialog(gamePanel, new JLabel("Previous changes will not be saved. Do you really want to proceed to the previous puzzle?"), "Warning!", JOptionPane.OK_CANCEL_OPTION);
			if(response == JOptionPane.OK_OPTION){
				currentPuzzle--;
				currentBoard = sudokuBoards.get(currentPuzzle);
				isSpecialSudokuActivated = false;
				currentStateOfTable = SudokuUtils.copyPuzzle(currentBoard.getPuzzle());
				resetPuzzle();
				currentType = gamePanel.getTypeComboBox().getItemAt(0).toString();
				gamePanel.getTypeComboBox().setSelectedItem(currentType);
				
				if(currentPuzzle <= 0) gamePanel.getPrevPuzzleButton().setEnabled(false);
				if(currentPuzzle < sudokuBoards.size()-1) gamePanel.getNextPuzzleButton().setEnabled(true);			
			}
		} else if(event.getSource() == gamePanel.getTypeComboBox()){
			if(gamePanel.getTypeComboBox().getSelectedItem().toString().equals("X")){
				if(SudokuUtils.checkPuzzle(currentBoard, true, false, false).size() != 0){
					JOptionPane.showMessageDialog(gamePanel, "Puzzle not applicable for X Sudoku");
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
					return;
				}
				
				if(currentBoard.getxSolution().size() == 0){
					JOptionPane.showMessageDialog(gamePanel, "Puzzle has no solution for X Sudoku");
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
					return;
				}
			} else if(gamePanel.getTypeComboBox().getSelectedItem().toString().equals("Y")){
				if(currentBoard.getPuzzleSize()%2 == 0 || SudokuUtils.checkPuzzle(currentBoard, false, true, false).size() != 0){
					JOptionPane.showMessageDialog(gamePanel, "Puzzle not applicable for Y Sudoku");
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
					return;
				}
				
				if(currentBoard.getySolution().size() == 0){
					JOptionPane.showMessageDialog(gamePanel, "Puzzle has no solution for Y Sudoku");
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
					return;
				}
				
			} else if(gamePanel.getTypeComboBox().getSelectedItem().toString().equals("XY")){
				if(currentBoard.getPuzzleSize()%2 == 0 || SudokuUtils.checkPuzzle(currentBoard, true, true, false).size() != 0){
					JOptionPane.showMessageDialog(gamePanel, "Puzzle not applicable for XY Sudoku");
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
					return;
				}
				
				if(currentBoard.getxYSolution().size() == 0){
					JOptionPane.showMessageDialog(gamePanel, "Puzzle has no solution for XY Sudoku");
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
					return;
				}
			}
		
			if(!gamePanel.getTypeComboBox().getSelectedItem().toString().equals(currentType)){
				int response = JOptionPane.showConfirmDialog(gamePanel, new JLabel("Previous changes will not be saved. Do you really want to change the puzzle type?"), "Warning!", JOptionPane.OK_CANCEL_OPTION);
				if(response == JOptionPane.OK_OPTION){
					currentType = (String) gamePanel.getTypeComboBox().getSelectedItem();
					isSpecialSudokuActivated = false;
					currentStateOfTable = SudokuUtils.copyPuzzle(currentBoard.getPuzzle());
					resetPuzzle();
				} else if(response == JOptionPane.CANCEL_OPTION){
					gamePanel.getTypeComboBox().setSelectedItem(currentType);
				}
		
			}
		} else if (event.getSource().equals(gamePanel.getCheckerButton())){
			boolean xSudoku = false;
			boolean ySudoku = false;
			
			switch (currentType) {
				case "X":
					xSudoku = true;
					ySudoku = false;
					break;
	
				case "Y":
					xSudoku = false;
					ySudoku = true;
					break;
				
				case "XY":
					xSudoku = true;
					ySudoku = true;
					break;
			}
			
			SudokuBoard currentStateOfBoard = new SudokuBoard(currentBoard.getPuzzleSize(), currentStateOfTable);
			errorCells = SudokuUtils.checkPuzzle(currentStateOfBoard, xSudoku, ySudoku, true);
		} else if(event.getSource() == gamePanel.getResetButton()){
			resetPuzzle();
		} else if (event.getSource().equals(gamePanel.getActivateSpecialButton())){
			isSpecialSudokuActivated = !isSpecialSudokuActivated;
			drawTable(currentStateOfTable);
		}
		
		if(event.getSource() == gamePanel.getTimer()){
			tickCount++;
			gamePanel.getTimerLabel().setText(gamePanel.computeDuration(tickCount));
		}
	}
		
	public void drawTable(int[][] puzzle){
		int puzzleSize = puzzle.length;
		Object data[][] = new Object[puzzleSize][puzzleSize];
		String header[] = new String[puzzleSize];
		for(int i = 0; i < puzzleSize; i++){
			for(int j = 0;  j < puzzleSize; j++){
				Object value = String.valueOf(puzzle[i][j]);
				if(puzzle[i][j] == 0){
					value = "";
				} 
				
				if(isSpecialSudokuActivated){
					value = new ImageIcon("resources/images/instructors/" + puzzle[i][j] + ".png");
				}
				
				data[i][j] = value;
			}
			header[i] = "";
		}
		
		DefaultTableModel model = new DefaultTableModel(data, header) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
            	if(isSpecialSudokuActivated){
            		return Icon.class;
            	} else {
            		return String.class;
            	}
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
			
	}

	@Override
	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		
		if(keyCode >= 49 && keyCode < currentBoard.getPuzzleSize()+49){
			int row = gamePanel.getSudokuTable().getSelectedRow();
			int column = gamePanel.getSudokuTable().getSelectedColumn();
			
			if(row >= 0 && column >= 0 && currentBoard.getPuzzle()[row][column] == 0){
				Object value = String.valueOf(keyCode-48);
				
				currentStateOfTable[row][column] = keyCode-48;
				
				if(isSpecialSudokuActivated){
					value = new ImageIcon("resources/images/instructors/" + (keyCode-48) + ".png");
				}
				
				gamePanel.getSudokuTable().getModel().setValueAt(value, row, column);
				
			}
			
			SudokuBoard board = new SudokuBoard(currentBoard.getPuzzleSize(), currentStateOfTable);
			int noOfErrors = 0;
			if(currentType.equals("Normal")){
				noOfErrors = SudokuUtils.checkPuzzle(board, false, false, false).size();
			} else if(currentType.equals("X")){
				noOfErrors = SudokuUtils.checkPuzzle(board, true, false, false).size();
			} else if(currentType.equals("Y")){
				noOfErrors = SudokuUtils.checkPuzzle(board, false, true, false).size();
			} else if(currentType.equals("XY")){
				noOfErrors = SudokuUtils.checkPuzzle(board, true, true, false).size();
			}
			
		if(noOfErrors == 0){
				int [][] checkComplete = currentStateOfTable;
				boolean areInputsComplete = true;
				
				for (int i = 0; i < checkComplete.length; i++) {
					for (int j = 0; j < checkComplete[i].length; j++) {
						if (checkComplete[i][j] == 0)
							areInputsComplete = false;
					}
				}
				
				if(areInputsComplete){
					String[] options = {"OK"};
					JPanel panel = new JPanel();
					JLabel label = new JLabel("Enter Your name : ");
					JTextField textField = new JTextField(10);
					panel.add(label);
					panel.add(textField);
					JOptionPane.showOptionDialog(null, panel, "You solved the puzzle in " + tickCount + " seconds!", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
				
					FileAccess fileAccess = new FileAccess();
					try {
						String[] data = new String[]{textField.getText(), tickCount + "", currentBoard.getPuzzleSize() + "", currentType};
						fileAccess.writeScore(data, "resources/highscores.dat");
					} catch (IOException e) {
						System.out.println("[ Error writing high scores! ]");
					}					
				}
			}
			
		} else if(keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE){
			int row = gamePanel.getSudokuTable().getSelectedRow();
			int column = gamePanel.getSudokuTable().getSelectedColumn();
			
			currentStateOfTable[row][column] = 0;
			
			if(row >= 0 && column >= 0 && currentBoard.getPuzzle()[row][column] == 0){
				Object value = "";
				
				if(isSpecialSudokuActivated){
					value = new ImageIcon("resources/images/instructors/0.png");
				}
				
				gamePanel.getSudokuTable().getModel().setValueAt(value, row, column);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	public class CellRender extends DefaultTableCellRenderer  { 
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean   isSelected, boolean hasFocus, int row, int column){ 
		    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
		    
		    int fontSize = (int) ((gamePanel.getSudokuTable().getPreferredSize().getWidth()/gamePanel.getSudokuTable().getRowCount())*0.50);
		    if(currentBoard.getPuzzle()[row][column] != 0){
		    	component.setFont(new Font("Verdana", Font.BOLD, fontSize));
		    } else {
		    	component.setFont(new Font("Verdana", Font.PLAIN, fontSize));
		    }
		    
			if (((row / (int) Math.sqrt(currentBoard.getPuzzleSize())) % 2 == 0 && (column / (int) Math.sqrt(currentBoard.getPuzzleSize())) % 2 == 0)
					|| ((row / (int) Math.sqrt(currentBoard.getPuzzleSize())) % 2 == 1 && (column / (int) Math.sqrt(currentBoard.getPuzzleSize())) % 2 == 1)) {
		        component.setBackground(new Color(210, 210, 210)); 
		    } else {
		    	component.setBackground(new Color(240,240,240));
		    }
	        
			if (currentType.equals("X") || currentType.equals("XY")) {
				if (row == column || column == currentBoard.getPuzzleSize()-1-row) {
					component.setBackground(new Color(0, 255, 0));
				}
			}
		    
			if(currentType.equals("Y") || currentType.equals("XY")){
		    	int center = (int) (Math.sqrt(currentBoard.getPuzzleSize())+1);
		    	if( (row < center && (row==column || column == currentBoard.getPuzzleSize()-1-row)) || (row >= center && column == center)){
		    		component.setBackground(new Color(0,255,0));	
		    	}
			}
		    
		    if(errorCells.contains(new Point(row, column))){
		    	component.setBackground(new Color(210, 0, 0));
		    } 
		    
		    gamePanel.getSudokuTable().repaint();
		  
		    return component; 
		}
	}
	
	
	public void initialize(String filePath)  {
		FileAccess fileAccess = new FileAccess();
		try {
			sudokuBoards = fileAccess.readBoard(filePath);
			SudokuUtils.findSolutions(sudokuBoards);
		} catch (IOException e) {
			System.out.println("[ Error reading input sudoku file! ]");
		}
		
		if(sudokuBoards.size() > 0){
			currentBoard = sudokuBoards.get(currentPuzzle);	
			currentStateOfTable = SudokuUtils.copyPuzzle(currentBoard.getPuzzle());
			
			drawTable(currentBoard.getPuzzle());
			
			if(sudokuBoards.size() > 1) gamePanel.getNextPuzzleButton().setEnabled(true);
			gamePanel.getTimer().start();
		}
	}
	
	public void resetPuzzle(){
		tickCount = 0;
		drawTable(currentBoard.getPuzzle());
		gamePanel.getNextSolutionButton().setEnabled(false);
		gamePanel.getPrevSolutionButton().setEnabled(false);
		errorCells.clear();
	}
}
