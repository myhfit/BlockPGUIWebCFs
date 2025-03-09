package bp.ui.form;

import static bp.util.LogicUtil.IFVU;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import bp.data.BPWebSiteAnalyzer;
import bp.ui.dialog.BPDialogSelectData;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPFormPanelWebSiteLink extends BPFormPanelResourceBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8715120789869608482L;

	protected BPTextField m_txthost;
	protected BPTextField m_txtport;
	protected BPTextField m_txtuser;
	protected BPTextField m_txtpass;
	protected BPTextFieldPane m_pananalyzer;
	protected BPTextField m_txtanalyzer;
	protected BPComboBox<String> m_cmbprotocol;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("host", m_txthost.getNotEmptyText());
		IFVU(ObjUtil.toInt(m_txtport.getNotEmptyText(), 80), (v) -> rc.put("port", v));
		rc.put("user", m_txtuser.getNotEmptyText());
		rc.put("password", m_txtpass.getNotEmptyText());
		rc.put("protocol", m_cmbprotocol.getSelectedItem());
		rc.put("analyzer", m_txtanalyzer.getNotEmptyText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();
		m_txthost = makeSingleLineTextField();
		m_txtport = makeSingleLineTextField();
		m_txtuser = makeSingleLineTextField();
		m_txtpass = makeSingleLineTextField();
		m_cmbprotocol = makeComboBox(null);
		m_pananalyzer = makeSingleLineTextFieldPanel(this::onAnalyzerFind);
		m_txtanalyzer = m_pananalyzer.getTextComponent();

		BPComboBoxModel<String> model = (BPComboBoxModel<String>) m_cmbprotocol.getModel();
		model.setDatas(Arrays.asList("http", "https"));
		addLine(new String[] { "Host" }, new Component[] { m_txthost }, () -> !m_txthost.isEmpty());
		addLine(new String[] { "Port" }, new Component[] { m_txtport });
		addLine(new String[] { "Username" }, new Component[] { m_txtuser });
		addLine(new String[] { "Password" }, new Component[] { m_txtpass });
		addLine(new String[] { "Protocol" }, new Component[] { m_cmbprotocol });
		addSeparator("Advance");
		addLine(new String[] { "Analyzer" }, new Component[] { m_pananalyzer });
	}

	protected CompletionStage<List<String>> getAnalyzerClasses()
	{
		return CompletableFuture.supplyAsync(() ->
		{
			List<String> rc = new ArrayList<String>();
			List<String> classnames = ClassUtil.getClassNames("bp.data", true);
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			for (String classname : classnames)
			{
				if (!rc.contains(classname))
				{
					if (ClassUtil.checkChildClass(BPWebSiteAnalyzer.class, classname, cl))
						rc.add(classname);
				}
			}
			return rc;
		});
	}

	protected String onAnalyzerFind(String old)
	{
		List<String> analyzerlist = UIUtil.block(this::getAnalyzerClasses, "Searching Analyzer Class...");
		BPDialogSelectData<String> dlg = new BPDialogSelectData<>();
		dlg.setSource(analyzerlist == null ? new ArrayList<String>() : analyzerlist);
		dlg.setTitle("Select Analyzer Class Name");
		dlg.setVisible(true);
		return dlg.getSelectData();
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		setComponentValue(m_txtname, data, "name", editable);
		setComponentValue(m_txthost, data, "host", editable);
		setComponentValue(m_txtport, data, "port", editable);
		setComponentValue(m_txtuser, data, "user", editable);
		setComponentValue(m_txtpass, data, "password", editable);
		setComponentValue(m_cmbprotocol, data, "protocol", editable);
		setComponentValue(m_txtanalyzer, data, "analyzer", editable);
	}
}