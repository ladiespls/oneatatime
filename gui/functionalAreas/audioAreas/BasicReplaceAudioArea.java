package gui.functionalAreas.audioAreas;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import defaults.Defaults;
import gui.VideoControlArea;
import gui.functionalAreas.AbstractFunctionalArea;
import gui.functionalAreas.workers.ReplaceAudioWorker;

/*
 BASIC:
 URL field 
Replace Button
 Cancel Button
 Error message (file exists, failed etc)
 */

/**
 * This class represents the basic replace pane. It contains the create method for painting this pane,
 * allowing for a file to be selected and the operation to be carried out. This operation is carried out
 * by grabbing the values of the fields of AdvancedReplaceArea and passing them into a worker with fields
 * from this class and the player. Upon completion error/success is reported via processWorkerResults.
 * @author fsta657
 *
 */
@SuppressWarnings("serial")
public class BasicReplaceAudioArea extends AbstractFunctionalArea implements
		ActionListener {

	private JFileChooser _fileChooser;
	private JButton _replace;
	private JTextField _currentFile;
	private JButton _choose;
	private JButton _cancel;
	private JButton _preview;
	private JProgressBar _progressBar;
	// Worker Fields
	private ReplaceAudioWorker _worker;
	// Boolean Fields
	private boolean _canReplace = true;
	// Reference Fields
	private AdvancedReplaceAudioArea _ar;

	public BasicReplaceAudioArea(AdvancedReplaceAudioArea ar) {
		super();
		_ar = ar;
	}

	@Override
	protected JPanel createAreaSpecific() {

		// Set up file chooser
		_fileChooser = new JFileChooser();
		_fileChooser.addActionListener(this);

		// Set up area title
		JLabel areaTitle = new JLabel("Basic Replace Audio");
		areaTitle.setFont(Defaults.DefaultTitleFont);
		areaTitle.setForeground(Defaults.DefaultWritingColour);
		areaTitle.setHorizontalAlignment(JLabel.CENTER);
		areaTitle.setOpaque(false);

		// Set up start panel
		JLabel label3 = new JLabel("Select a file: ");
		label3.setBackground(Defaults.DefaultDownloadColour);
		label3.setFont(Defaults.DefaultLabelFont);
		label3.setForeground(Defaults.DefaultWritingColour);
		label3.setHorizontalAlignment(JLabel.CENTER);
		// Create choose button
		_choose = new JButton("Select");
		_choose.setOpaque(false);
		_choose.setFont(Defaults.DefaultButtonFont);
		_choose.setAlignmentX(Component.CENTER_ALIGNMENT);
		_choose.addActionListener(this);
		_preview = new JButton("Preview");
		_preview.setOpaque(false);
		_preview.setFont(Defaults.DefaultButtonFont);
		_preview.setAlignmentX(Component.CENTER_ALIGNMENT);
		_preview.addActionListener(this);
		// Combine into sub panel
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
		p1.add(label3);
		p1.add(_choose);
		p1.add(_preview);
		p1.setOpaque(false);
		// Create show file area
		_currentFile = new JTextField("No file selected");
		_currentFile.setFont(Defaults.DefaultTextFieldFont);
		_currentFile.setHorizontalAlignment(JLabel.CENTER);
		_currentFile.setBackground(Defaults.DefaultTextFieldColour);
		_currentFile.setForeground(Defaults.DefaultLoadColour);
		_currentFile.addActionListener(this);
		_currentFile.setPreferredSize(new Dimension(
				Defaults.DefaultFuncAreaWidth - 340, 30));
		// Combine into show file panel
		JLabel filePanel = new JLabel();
		filePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filePanel.add(p1);
		filePanel.add(_currentFile);
		filePanel.setOpaque(false);

		// Set up progress panel
		// Create replace button
		_replace = new JButton("Replace");
		_replace.setOpaque(false);
		_replace.setFont(Defaults.DefaultButtonFont);
		_replace.setAlignmentX(Component.CENTER_ALIGNMENT);
		_replace.addActionListener(this);
		_replace.setBackground(Defaults.DefaultGoButtonColour);
		// Create label
		JLabel label4 = new JLabel("Progress:");
		label4.setBackground(Defaults.DefaultDownloadColour);
		label4.setFont(Defaults.DefaultLabelFont);
		label4.setForeground(Defaults.DefaultWritingColour);
		label4.setHorizontalAlignment(JLabel.CENTER);
		// Create progress bar
		_progressBar = new JProgressBar();
		_progressBar.setForeground(Defaults.DefaultLoadColour);
		_progressBar.setBackground(Defaults.DefaultProgressColour);
		_progressBar.setPreferredSize(new Dimension(
				Defaults.DefaultFuncAreaWidth - 300, 30));
		// Create cancel button
		_cancel = new JButton("Cancel");
		_cancel.setOpaque(false);
		_cancel.setFont(Defaults.DefaultButtonFont);
		_cancel.setAlignmentX(Component.CENTER_ALIGNMENT);
		_cancel.addActionListener(this);
		_cancel.setEnabled(false);
		// Combine into progress panel
		JLabel progressPanel = new JLabel();
		progressPanel.setLayout(new FlowLayout());
		progressPanel.add(_replace);
		progressPanel.add(label4);
		progressPanel.add(_progressBar);
		progressPanel.add(_cancel);
		progressPanel.setOpaque(false);

		// Add panels
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(Defaults.DefaultFuncAreaWidth,
				Defaults.DefaultFuncAreaHeight));
		panel.setLayout(new GridLayout(4, 1, 0, 0));

		panel.add(areaTitle);
		panel.add(filePanel);
		panel.add(progressPanel);
		
		return panel;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// If source was replace button, start replace
		if (e.getSource().equals(_replace)) {
			if (_canReplace) {
				String inVid;
				if (_currentFile.getText().equals("No file selected")) {
					JOptionPane.showMessageDialog(null,
							"No audio file selected to overlay", "VAMIX Error",
							JOptionPane.ERROR_MESSAGE);
				} else if ((inVid = VideoControlArea.getPath()).equals("none")) {
					JOptionPane.showMessageDialog(null,
							"No video file currently selected", "VAMIX Error",
							JOptionPane.ERROR_MESSAGE);
				} else if (_ar.getOutputName().trim().equals("")){
					JOptionPane.showMessageDialog(this,
							"Please enter an output file name", "VAMIX Warning",
							JOptionPane.ERROR_MESSAGE);
				}else {
					String outVideo;
					String[] arr = inVid.split("\\.");
					String ext = "." + arr[arr.length - 1];
					if (_ar.getOutputName().trim().equals(
							"Enter in new output file name")) {
						outVideo = "output" + ext;
					} else {
						outVideo = _ar.getOutputName().trim() + ext;
					}
					if (!(_ar.getOutputLocation()
							.equals("No location selected"))) {
						outVideo = _ar.getOutputLocation() + "/" + outVideo;
					}
					File f = new File(outVideo);
					File q = new File(VideoControlArea.getPath());
					if (q.getAbsolutePath().equals(f.getAbsolutePath())){
						JOptionPane.showMessageDialog(null,
								"Input cannot have the same name as output, please rename.",
								"VAMIX Error", JOptionPane.ERROR_MESSAGE);
					}else{
					if (f.exists()){
						Object[] options = { "OK", "Cancel" };
						int selected = JOptionPane
								.showOptionDialog(
										null,
										"Warning: the file to be created will overwrite an existing file. Continue?",
										"Overwrite warning",
										JOptionPane.DEFAULT_OPTION,
										JOptionPane.WARNING_MESSAGE, null,
										options, options[0]);

						if (selected == 0) {
							// Ready to rock
							_canReplace = false;
							_progressBar.setIndeterminate(true);
							_replace.setEnabled(false);
							_worker = new ReplaceAudioWorker(inVid,
									_currentFile.getText(), outVideo, this);
							_worker.execute();
						}
					}else{
						// Ready to rock
						_canReplace = false;
						_progressBar.setIndeterminate(true);
						_replace.setEnabled(false);
						_worker = new ReplaceAudioWorker(inVid,
								_currentFile.getText(), outVideo, this);
						_worker.execute();
					}
					}
				}
			}
		}
		// If source is button to open file chooser, open the chooser
		else if (e.getSource().equals(_choose)) {
			FileFilter filter = new FileNameExtensionFilter("Audio",
					Defaults.DefaultAudioFileTypes);
			_fileChooser.setFileFilter(filter);
			if (!VideoControlArea.location.equals("none")){
				File f = new File(VideoControlArea.location);
				_fileChooser.setCurrentDirectory(f);
			}
			// NEEDS GOOD TEXT
			_fileChooser.showOpenDialog(this);
		}
		// If source is file chooser, update text field
		else if (e.getSource().equals(_fileChooser)) {
			if (_fileChooser.getSelectedFile() != null) {
				_currentFile.setText(_fileChooser.getSelectedFile().getPath());
				VideoControlArea.location = _fileChooser.getSelectedFile().getPath();
			}
		}
		else if (e.getSource().equals(_preview)) {
			if (_currentFile.getText().equals("No file selected")
					|| _currentFile.getText().isEmpty()) {
				// Tell user they have to enter a file
				JOptionPane.showMessageDialog(this, "Enter a file",
						"VAMIX Warning", JOptionPane.ERROR_MESSAGE);
			}else{
				ProcessBuilder builder = new ProcessBuilder("vlc", _currentFile.getText());
				try {
					builder.start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}else if (e.getSource().equals(_cancel)) {
			_worker.cancel(true);
		}
	}

	public void processWorkerResults(int exitStatus) {
		// If non-zero, failure
		if (exitStatus == 9001){
			JOptionPane.showMessageDialog(null, "Replacing audio successfully cancelled",
					"VAMIX Success", JOptionPane.INFORMATION_MESSAGE);
		}
	else if (exitStatus != 0) {
			JOptionPane.showMessageDialog(null,
					"Replacing audio failed due to system error",
					"VAMIX Error", JOptionPane.ERROR_MESSAGE);
		} else {
			// Otherwise success
			JOptionPane.showMessageDialog(null, "Audio successfully replaced",
					"VAMIX Success", JOptionPane.INFORMATION_MESSAGE);
		}
		// Enable replacing
		_canReplace = true;
		_progressBar.setIndeterminate(false);
		_replace.setEnabled(true);
	}

}
