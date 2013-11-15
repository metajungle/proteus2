package com.iai.proteus.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

public class JobProgressToolControl implements IProgressMonitor {

	private ProgressBar progressBar;

	@Inject 
	UISynchronize sync;

	@PostConstruct
	public void createControls(Composite parent) {
		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setBounds(100, 10, 200, 20);
		progressBar.setVisible(false);
	}

	@Override
	public void worked(final int work) {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				progressBar.setSelection(progressBar.getSelection() + work);
			}
		});
	}

	@Override
	public void subTask(String name) {

	}

	@Override
	public void setTaskName(String name) {

	}

	@Override
	public void setCanceled(boolean value) {

	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void internalWorked(double work) {
	}

	@Override
	public void done() {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				progressBar.setEnabled(false);
				progressBar.setVisible(false);
				progressBar.setSelection(100);
			}
		});
	}

	@Override
	public void beginTask(final String name, final int totalWork) {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				progressBar.setVisible(true);
				progressBar.setEnabled(true);
				progressBar.setMaximum(totalWork);
				progressBar.setToolTipText(name);
			}
		});
	}
} 