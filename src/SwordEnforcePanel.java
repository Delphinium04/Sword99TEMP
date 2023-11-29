import java.math.*;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SwordEnforcePanel extends JPanel {
	private static ActionListener e_panelSwitch;
	final String IMAGEPATH = "./image/enforce/";
	final String BGIMAGEPATH = "./image/enforce/EnforceBG.png";
	final int DEFAULT_STR_STAT = 0;
	final int DEFAULT_CRIT_STAT = 0;

	int str;
	int crit;

	Space[][] board;
	Dice[] dices;
	int[][] b_boardValues;
	int[] b_diceValues;
	Dice selectedDice = null;

	Image backgroundImage;
	Icon[] spaceImages = new ImageIcon[2];
	Icon spaceImage;
	Icon[] diceImages = new ImageIcon[6];

	class Space extends JButton implements ActionListener {
		int number = 0;

		public Space(int value, int x, int y, int w, int h) {
			super();
			set(value);
			setBounds(x, y, w, h);

			// https://stackoverflow.com/questions/2713480/is-it-possible-to-put-text-on-top-of-a-image-in-a-button
			setHorizontalTextPosition(SwingConstants.CENTER);
		}

		int get() {
			return number;
		}

		void set(int arg) {
			number = arg;
			setText(String.valueOf(number));
			setFont(new Font("Arial", Font.BOLD, 30));
		}

		void resetShape() {
			setIcon(spaceImages[0]);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.print("Space Selected ");
			if (selectedDice != null) {
				System.out.print("With Dice");
				setSpaceNumber(this);
				setIcon(spaceImages[1]);
			}
			System.out.println();
		}
	}

	class Dice extends JButton implements ActionListener {
		int number = 0;

		public Dice(int value, int x, int y, int w, int h) {
			super();
			set(value);
			setBounds(x, y, w, h);
			setText(null);
			setBorder(new LineBorder(Color.ORANGE, 10));
		}

		int get() {
			return number;
		}

		void set(int arg) {
			number = arg;
		}

		void resetShape() {
			setIcon(diceImages[get() - 1]);
			// https://stackoverflow.com/questions/33954698/jbutton-change-default-border
			setBorder(new LineBorder(Color.ORANGE, 2));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedDice != null)
				selectedDice.resetShape();
			selectedDice = this;
			setBorder(new LineBorder(Color.RED, 2));
			System.out.println("Dice Selected");
		}

	}


	SwordEnforcePanel(Image img, ActionListener switchPanelListener) {
		setLayout(null);

		// Image Setting
		for (int i = 0; i < 6; i++) {
			diceImages[i] = new ImageIcon(IMAGEPATH + "Dice" + String.valueOf(i + 1) + ".jpg");
		}
		spaceImage = new ImageIcon(IMAGEPATH + "space.png");
		spaceImages[0] = spaceImage;
		spaceImages[1] = spaceImage;
		backgroundImage = new ImageIcon(BGIMAGEPATH).getImage();

		// Variable assign
		e_panelSwitch = switchPanelListener;
		str = DEFAULT_STR_STAT;
		crit = DEFAULT_CRIT_STAT;
		board = new Space[3][3];
		dices = new Dice[3];
		b_boardValues = new int[3][3];
		b_diceValues = new int[3];

		Arrays.fill(b_diceValues, 0);
		for (int i = 0; i < 3; i++)
			Arrays.fill(b_boardValues[i], 0);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Space s = new Space(0, 250 + j * 150, 150 + i * 150, 120, 120);
				s.addActionListener(s);
				s.setIcon(spaceImage);
				s.setBorder(null);
				add(s);
				board[i][j] = s;
			}
			dices[i] = new Dice(0, 750 + i * 150, 250, 120, 120);
			dices[i].addActionListener(dices[i]);
			dices[i].setIcon(diceImages[0]);
			dices[i].setBorder(null);
			add(dices[i]);
		}

		JButton resetBtn = new JButton("Reset");
		resetBtn.setBounds(800, 400, 120, 40);
		resetBtn.addActionListener(e -> {
			reset();
		});
		add(resetBtn);

		JButton continueBtn = new JButton("Continue");
		continueBtn.setBounds(950, 400, 120, 40);
		continueBtn.addActionListener(e -> {
			onContinueBtnClicked();
		});
		add(continueBtn);

		JLabel guide1 = new JLabel("+5");
		guide1.setIcon(spaceImages[0]);
		guide1.setBounds(100, 150, 120, 120);
		guide1.setHorizontalTextPosition(SwingConstants.CENTER);
		guide1.setFont(new Font("Arial", Font.BOLD, 25));
		guide1.setBorder(null);
		add(guide1);

		JLabel guide2 = new JLabel("*2");
		guide2.setIcon(spaceImages[0]);
		guide2.setBounds(100, 300, 120, 120);
		guide2.setHorizontalTextPosition(SwingConstants.CENTER);
		guide2.setFont(new Font("Arial", Font.BOLD, 25));
		guide2.setBorder(null);
		add(guide2);

		JLabel guide3 = new JLabel("CRIT*3");
		guide3.setIcon(spaceImages[0]);
		guide3.setBounds(100, 450, 120, 120);
		guide3.setHorizontalTextPosition(SwingConstants.CENTER);
		guide3.setFont(new Font("Arial", Font.BOLD, 25));
		guide2.setBorder(null);
		add(guide3);

		launch();
	}

	// https://stackoverflow.com/questions/22162398/how-to-set-a-background-picture-in-jpanel
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}

	public void launch() {
		selectedDice = null;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j].resetShape();
				b_boardValues[i][j] = board[i][j].get();
			}
			dices[i].setEnabled(true);
			dices[i].set((int) (Math.random() * 6) + 1);
			dices[i].resetShape();
			b_diceValues[i] = dices[i].get();
		}
	}

	void reset() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j].set(b_boardValues[i][j]);
				board[i][j].resetShape();
			}
			dices[i].set(b_diceValues[i]);
			dices[i].resetShape();
			dices[i].setEnabled(true);
		}
	}

	// Called at space actionPerformed
	void setSpaceNumber(Space targetSpace) {
		targetSpace.set(selectedDice.get());
		selectedDice.resetShape();
		selectedDice.setEnabled(false);
		selectedDice = null;
	}

	void saveStat() {
		str = DEFAULT_STR_STAT;
		crit = DEFAULT_CRIT_STAT;

		for (int i = 0; i < 3; i++) {
			str += board[1][i].get() * 2;
			crit += board[2][i].get() * 3;
			if (board[0][i].get() != 0)
				str += board[0][i].get() + 5;
		}
	}

	public void onContinueBtnClicked() {
		saveStat();
		String msg = String.format("공격력은 %d, 크리티컬 확률은 %d%%입니다.", str, crit);
		// JDialog
		JOptionPane opt = new JOptionPane();
		int input = JOptionPane.showConfirmDialog(null, msg, "진행할까요?", JOptionPane.YES_NO_OPTION);
		if (input == JOptionPane.YES_OPTION) {
			Sword_Main.battlePanel.launch(str, crit);
			e_panelSwitch.actionPerformed(null);
		}
	}
}