package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;

public class BPFormPanelTaskSendPing extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4145170806261521484L;

	protected BPTextField m_txthost;
	protected BPCheckBox m_chknanosec;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("host", m_txthost.getText());
		rc.put("nanosec", m_chknanosec.isSelected());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_txthost = makeSingleLineTextField();
		m_chknanosec = makeCheckBox();

		addLine(new String[] { "Host" }, new Component[] { m_txthost }, () -> !m_txthost.isEmpty());
		addLine(new String[] { "Nano Second" }, new Component[] { m_chknanosec });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txthost, data, "host", editable);
		setComponentValue(m_chknanosec, data, "nanosec", editable);
	}
}
