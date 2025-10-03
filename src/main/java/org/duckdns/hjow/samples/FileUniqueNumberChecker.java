package org.duckdns.hjow.samples;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigInteger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.base.DefaultProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;

public class FileUniqueNumberChecker extends DefaultProgram {
	private static final long serialVersionUID = 1429179997900728792L;
	protected JDialog dialog;
	protected JTextField tfName;
	protected JButton btnSel, btnRun;
	protected JProgressBar prog;
	protected JTextArea ta;
	protected JFileChooser fileChooser;
	
	protected volatile transient File file;
	
	public FileUniqueNumberChecker(SampleJavaCodes superInstance) {
		super(superInstance);
	}
	
	@Override
    public void init(SampleJavaCodes superDialog) {
		dialog = new JDialog(superDialog.getWindow());
		dialog.setSize(400, 300);
		dialog.setTitle(getTitle());
		dialog.setLayout(new BorderLayout());
		
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		dialog.add(pnMain, BorderLayout.CENTER);
		
		JPanel pnCenter = new JPanel();
		JPanel pnUp    = new JPanel();
		pnCenter.setLayout(new BorderLayout());
		pnUp.setLayout(new BorderLayout());
		pnMain.add(pnCenter, BorderLayout.CENTER);
		pnMain.add(pnUp    , BorderLayout.NORTH);
		
		JToolBar toolbar = new JToolBar();
		pnUp.add(toolbar, BorderLayout.CENTER);
		
		tfName = new JTextField(20);
		btnSel = new JButton("...");
		btnRun = new JButton("계산");
		prog   = new JProgressBar();
		ta     = new JTextArea();
		
		toolbar.add(tfName);
		toolbar.add(btnSel);
		toolbar.add(btnRun);
		pnUp.add(prog, BorderLayout.SOUTH);
		
		ta.setEditable(false);
		ta.setLineWrap(true);
		pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		
		btnSel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSelectFileRequest();
			}
		});
		
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRunRequested();
			}
		});
	}
	
	@Override
    public void alert(String msg) { 
		super.alert(msg);
		JOptionPane.showMessageDialog(getDialog(), msg);
	};
	
	@Override
	public String getTitle() {
		return "파일 고유번호 계산기";
	}

	@Override
	public String getName() {
		return "FileUniqueNumberChecker";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public JDialog getDialog() {
		return dialog;
	}
	
	protected void onSelectFileRequest() {
		int sel = fileChooser.showOpenDialog(getDialog());
		if(sel == JFileChooser.APPROVE_OPTION) {
			tfName.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	protected void onRunRequested() {
		file = new File(tfName.getText());
		if(DataUtil.isEmpty(tfName.getText())) { alert("파일을 선택 후 이용해 주세요."); return; }
		if(! file.exists()) { alert("존재하지 않는 파일입니다."); return; }
		if(file.isDirectory()) { alert("디렉토리, 폴더의 값은 계산할 수 없습니다."); return; }
        
		btnRun.setEnabled(false);
		btnSel.setEnabled(false);
		tfName.setEditable(false);
		ta.setText("");
		prog.setIndeterminate(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				process(file);
			}
		}).run();
	}
	
	public BigInteger process(File file) {
		BigInteger res = null;
		String str = null;
		
		try {
		    res = FileUtil.getFileCheckerNumber(file);
		    str = res.toString();
		} catch(Exception ex) {
			ex.printStackTrace();
			str = "오류 : " + ex.getMessage();
		}
		
		final String finals = str;
		if(getDialog() != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ta.setText(finals);
					prog.setIndeterminate(false);
					btnRun.setEnabled(true);
					btnSel.setEnabled(true);
					tfName.setEditable(true);
				}
			});
		}
		return res;
	}
}
