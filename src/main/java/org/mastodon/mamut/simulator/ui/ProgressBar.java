/*-
 * #%L
 * Mastodon Simulator
 * %%
 * Copyright (C) 2023 - 2025 Vladimir Ulman
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.mamut.simulator.ui;

import javax.swing.*;
import java.awt.*;

public class ProgressBar {
	public ProgressBar(final int min, final int max, final String label) {
		//creates the dialog
		mainFrame = new JFrame("Simulation progress bar");
		mainFrame.setLayout(new GridBagLayout());

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,10,10,10);
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 1;

		pbElem = new JProgressBar(min,max);
		mainFrame.add(pbElem,c);

		c.insets = new Insets(0,10,10,10);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;

		labelElem = new JLabel(label);
		mainFrame.add(labelElem,c);

		c.gridx = 2;

		JButton button = new JButton("Stop");
		button.addActionListener(l -> isStopBtnPressed = true);
		mainFrame.add(button,c);

		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private final JFrame mainFrame;
	private final JProgressBar pbElem;
	private final JLabel labelElem;
	private boolean isStopBtnPressed = false;

	public boolean isStop() {
		return isStopBtnPressed;
	}

	public void updateLabel(final String newLabel) {
		labelElem.setText(newLabel);
	}

	public void setProgress(final int val) {
		pbElem.setValue(val);
	}

	public void close() {
		mainFrame.setVisible(false);
		mainFrame.dispose();
	}
}
