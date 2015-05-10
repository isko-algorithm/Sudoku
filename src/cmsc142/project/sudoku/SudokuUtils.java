package cmsc142.project.sudoku;

import java.util.List;

public abstract class SudokuUtils {
	
	public static void checkGrid(List<Integer> candidates, int[][] puzzle, int puzzleSize, int row, int column){
		int subGridSize = (int) Math.sqrt(puzzleSize);
		int gridRow = (int) (row/subGridSize) * (subGridSize);
		int gridColumn = (int) (column/subGridSize) * (subGridSize);
		
		for(int i = gridRow; i < gridRow + subGridSize; i++ ){
			for(int j = gridColumn; j < gridRow + subGridSize; j++ ){
				if(puzzle[i][j] != 0 && !(i == row || j == column) ){
					candidates.remove(puzzle[i][j]);
				}
			}
		}
		
	}
	
	public static void checkRow(){
		
	}

	public static void checkColumn(){
	
	}
}
