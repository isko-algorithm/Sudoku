package cmsc142.project.sudoku;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

public class HighScorePanelController implements ActionListener {
	private HighScorePanel highScorePanel;
	private HashMap<String, ArrayList<ArrayList<String[]>>> highscores;
	
	public HighScorePanelController(){
		updateHighScore();
		this.highScorePanel.getHighScoreSizeComboBox().addActionListener(this);
		this.highScorePanel.getHighScoreTypeComboBox().addActionListener(this);
	}
	
	public void updateHighScore() {
		FileAccess fileAccess = new FileAccess();
		ArrayList<ArrayList<String[]>> highScorePerPuzzleType = null;
		
		String[] typeList = SudokuType.getValues();
		String[] sizeList = null;
		
		try {
			highscores = fileAccess.readScoreData("resources/highscores.dat");
			
			Object[] keys = highscores.keySet().toArray();
			
			sizeList = new String[keys.length];
			
			for (int i = 0; i < keys.length; i++) {
				sizeList[i] = (String) keys[i];
			}
			
		} catch (IOException e) {
			System.out.println("Error reading file! " + e.getMessage());
		}
		
		this.highScorePanel = new HighScorePanel(typeList, sizeList);		
		updateTable();
	}

	private void updateTable(){
		String sizeSelected = this.highScorePanel.getHighScoreSizeComboBox().getSelectedItem().toString();
		String typeSelected = this.highScorePanel.getHighScoreTypeComboBox().getSelectedItem().toString();
		ArrayList<String[]> highScoreList = highscores.get(sizeSelected).get(SudokuType.valueOf(typeSelected).getValue());
		
		String body[][] = new String[highScoreList.size()][2];
		String header[] = {"Name", "Time"};
		
		for(int i = 0; i < highScoreList.size(); i++){
			body[i] = highScoreList.get(i).clone();
		}
		
		DefaultTableModel dataModel = new DefaultTableModel(body, header) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
		
		this.highScorePanel.getHighScoreTable().setModel(dataModel);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(this.highScorePanel.getHighScoreSizeComboBox()) || event.getSource().equals(this.highScorePanel.getHighScoreTypeComboBox())){
			updateTable();
		}
	}

	
	public HighScorePanel getHighScorePanel() {
		return highScorePanel;
	}

	public void setHighScorePanel(HighScorePanel highScorePanel) {
		this.highScorePanel = highScorePanel;
	}
}
