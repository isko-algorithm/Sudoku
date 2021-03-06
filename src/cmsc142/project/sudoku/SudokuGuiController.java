package cmsc142.project.sudoku;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class SudokuGuiController implements ActionListener {
	private SudokuGui sudokuGui;
	private StartPanelController startPanelController;
	private HighScorePanelController highScorePanelController;
	private GamePanelController gamePanelController;
	
	public SudokuGuiController(){
		this.sudokuGui = new SudokuGui();
		
		this.highScorePanelController = new HighScorePanelController();
		this.startPanelController = new StartPanelController();
		this.gamePanelController = new GamePanelController();
		
		this.sudokuGui.changePanel(this.startPanelController.getStartPanel());
		
		/*
		 * Adds action listener for the transition of panels
		 */
		this.startPanelController.getStartPanel().getNewGame().addActionListener(this);
		this.startPanelController.getStartPanel().getHighScore().addActionListener(this);
		this.startPanelController.getStartPanel().getExit().addActionListener(this);
		
		this.highScorePanelController.getHighScorePanel().getBackButton().addActionListener(this);
		this.gamePanelController.getGamePanel().getBackMenuButton().addActionListener(this);
		playSound();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.startPanelController.getStartPanel().getNewGame())){
			JFileChooser fileChooser = new JFileChooser();
			int response = fileChooser.showOpenDialog(sudokuGui);
			if(response == JFileChooser.APPROVE_OPTION){
				ArrayList<File> fileList = new ArrayList<File>();
				File file = fileChooser.getSelectedFile();
//				this.sudokuGui.changePanel(new LoadingPanel());
				try {
					gamePanelController.initialize(file.getAbsolutePath());
					this.sudokuGui.changePanel(gamePanelController.getGamePanel());
				} catch (IOException e) {
					System.out.println("[ Cannot read file. ]");
					JOptionPane.showMessageDialog(sudokuGui, "Unable to load file.");
					this.sudokuGui.changePanel(startPanelController.getStartPanel());
					
				}
			}
		} else if (event.getSource().equals(this.startPanelController.getStartPanel().getHighScore())){
			Map attributes = this.highScorePanelController.getHighScorePanel().getNewFont().getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);	
			this.highScorePanelController.getHighScorePanel().getBackButton().setFont(this.highScorePanelController.getHighScorePanel().getNewFont().deriveFont(attributes));	
			highScorePanelController.updateTable();
			this.sudokuGui.changePanel(highScorePanelController.getHighScorePanel());
		} else if (event.getSource().equals(this.startPanelController.getStartPanel().getExit())){
			sudokuGui.dispose();
		} else if(event.getSource().equals(this.highScorePanelController.getHighScorePanel().getBackButton())){
			startPanelController.getStartPanel().getHighScore().setIcon(new ImageIcon("./resources/images/Buttons/Button_Score.png"));
			this.sudokuGui.changePanel(this.startPanelController.getStartPanel());
		} else if(event.getSource().equals(this.gamePanelController.getGamePanel().getBackMenuButton())){
			Font font = new Font("A Year Without Rain", Font.PLAIN, 20);
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);	
			this.gamePanelController.getGamePanel().getBackMenuButton().setFont(font.deriveFont(attributes));	
			startPanelController.getStartPanel().getNewGame().setIcon(new ImageIcon("./resources/images/Buttons/Button_New.png"));
			this.sudokuGui.changePanel(this.startPanelController.getStartPanel());
		}
	}
	
	private void playSound() {
	    String soundName = "./resources/audio/mp3/bg.wav";    
	    AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(soundName));
			Clip clip = AudioSystem.getClip();
		    clip.open(audioInputStream);
		    clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	    
  
	}
}
