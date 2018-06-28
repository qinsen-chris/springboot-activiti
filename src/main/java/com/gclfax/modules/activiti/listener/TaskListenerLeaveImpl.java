package com.gclfax.modules.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskListenerLeaveImpl implements TaskListener {

	private static final long serialVersionUID = 2321705232653248727L;

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		delegateTask.setAssignee("李总");
	}

}
