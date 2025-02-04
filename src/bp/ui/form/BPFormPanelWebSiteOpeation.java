package bp.ui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPKVTable;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.web.BPWebOperation.CommonHTTPVerbs;

public class BPFormPanelWebSiteOpeation extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8927423165259602363L;

	protected BPTextField m_txtpath;
	protected BPComboBox<String> m_cmbverb;
	protected BPCodePane m_txtcontent;
	protected BPKVTable m_tabheaders;

	protected BiConsumer<String, Map<String, Object>> m_callback;

	public void setCallback(BiConsumer<String, Map<String, Object>> callback)
	{
		m_callback = callback;
	}

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("verb", m_cmbverb.getText());
		rc.put("path", m_txtpath.getNotEmptyText());
		rc.put("contentText", m_txtcontent.getText());

		return rc;
	}

	@SuppressWarnings("unchecked")
	public void showData(Map<String, ?> data, boolean editable)
	{
		setComponentValue(m_cmbverb, data, "verb", editable);
		setComponentValue(m_txtpath, data, "path", editable);
		setComponentValue(m_txtcontent, data, "contentText", editable);
		Map<String, Object> options = (Map<String, Object>) data.get("options");
		List<KV> kvs = new ArrayList<KV>();
		if (options != null)
		{
			List<List<String>> headerfields = (List<List<String>>) options.get("header");
			if (headerfields != null)
			{
				for (List<String> hf : headerfields)
				{
					KV kv = new KV();
					kv.key = hf.get(0);
					kv.value = hf.get(1);
					kvs.add(kv);
				}
			}
		}
		m_tabheaders.getBPTableModel().setDatas(kvs);
		m_tabheaders.refreshData();
	}

	protected boolean needScroll()
	{
		return false;
	}

	protected void initForm()
	{
		setGridWeakBorder(true);
		BPAction actsavecontent = BPAction.build("Save").callback((e) -> saveContent()).vIcon(BPIconResV.SAVE()).getAction();
		m_txtpath = makeSingleLineTextField();
		m_cmbverb = makeComboBox(null);
		m_txtcontent = new BPCodePane();
		m_tabheaders = new BPKVTable();
		JScrollPane scrollc = new JScrollPane(m_txtcontent);
		JScrollPane scroll = new JScrollPane(m_tabheaders);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_tabheaders.setMonoFont();
		m_tabheaders.addMouseListener(new UIUtil.BPMouseListener(this::onHeaderKVClick, null, null, null, null));
		m_txtcontent.setBorder(null);
		m_txtcontent.setMonoFont();
		scrollc.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollc.setPreferredSize(new Dimension(400, 400));
		((BPComboBoxModel<String>) m_cmbverb.getModel()).setDatas(Arrays.asList(CommonHTTPVerbs.verbs()));
		addLine(new String[] { "Verb" }, new Component[] { m_cmbverb }, () -> !m_cmbverb.getText().isEmpty());
		addLine(new String[] { "Path" }, new Component[] { m_txtpath }, () -> !m_txtpath.isEmpty());
		addSeparator("CONTENT", new Action[] { actsavecontent });
		doAddLineComponents(null, true, 0, new Component[] { scrollc });
		addSeparator("HEADER");
		doAddLineComponents(null, false, 0, new Component[] { scroll });
	}

	protected void saveContent()
	{
		BiConsumer<String, Map<String, Object>> callback = m_callback;
		if (callback != null)
		{
			callback.accept("contentText", getFormData());
		}
	}

	protected void onHeaderKVClick(MouseEvent e)
	{
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
		{
			List<KV> kvs = m_tabheaders.getSelectedDatas();
			if (kvs.size() > 0)
			{
				KV kv = kvs.get(0);
				int i = m_tabheaders.getSelectedColumn();
				if (i == 1)
				{
					Object obj = kv.value;
					UIStd.textarea(obj.toString(), "Detail");
				}
			}
		}
	}
}
