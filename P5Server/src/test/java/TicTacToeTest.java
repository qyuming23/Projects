import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TicTacToeTest {

	String[] s1;
	static FindNextMove FNM;

	@BeforeAll
	static void init() {
		FNM = new FindNextMove();
	}

	@BeforeEach
	void initStrings() {
		s1 = new String[9];

		for(int i = 0; i < 9; i++) {
			//initiate string
			s1[i] = "b";
		}
	}

	@Test
	void minMaxTest1() {
		assertEquals(1, FNM.getNextMove(s1), "Did not return best possible move");
	}

	@Test
	void minMaxTest2() {

		/*
		*       -----------
		*		X    X    b
		* 		b    b    b
		* 		b    b    b
		*       -----------
		 */

		s1[0] = "X";
		s1[1] = "X";

		//next best possible move is at position 3
		assertEquals(3, FNM.getNextMove(s1), "Did not return best possible move");
	}

	@Test
	void minMaxTest3() {

		/*
		 *       -----------
		 *		X    O    X
		 * 		b    X    b
		 * 		O    b    b
		 *       -----------
		 */

		s1[0] = "X";
		s1[1] = "O";
		s1[2] = "X";
		s1[4] = "X";
		s1[6] = "O";

		//next best possible move is at position 4
		assertEquals(4, FNM.getNextMove(s1), "Did not return best possible move");
	}



	//TODO: 5 more test cases are need, they can all be done just like above, but you gotta change the string array

}
