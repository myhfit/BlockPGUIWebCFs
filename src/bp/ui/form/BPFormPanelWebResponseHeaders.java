package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.config.UIConfigs;
import bp.ui.scomp.BPTextField;

public class BPFormPanelWebResponseHeaders extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2681655595256639571L;
	
	public BPFormPanelWebResponseHeaders()
	{
		m_labelwidth = (int) (120 * UIConfigs.UI_SCALE());
	}

	public Map<String, Object> getFormData()
	{
		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public void showData(Map<String, ?> data, boolean editable)
	{
		m_form.removeAll();
		if (data != null)
		{
			List<String> keys = new ArrayList<String>(data.keySet());
			keys.sort((a, b) -> (a == null ? "" : a).compareToIgnoreCase(b == null ? "" : b));
			for (String key : keys)
			{
				BPTextField comp = makeSingleLineTextField();
				List<String> values = (List<String>) data.get(key);
				comp.setEditable(editable);
				StringBuilder sb = new StringBuilder();
				for (String value : values)
				{
					if (sb.length() > 0)
						sb.append(" ");
					sb.append(value);
				}
				comp.setText(sb.toString());
				addLine(new String[] { key == null ? "" : key }, new Component[] { comp });
			}
		}
		validate();
		repaint();
	}

	protected void initForm()
	{
	}
}
