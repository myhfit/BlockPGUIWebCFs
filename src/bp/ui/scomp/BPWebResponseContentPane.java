package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.config.UIConfigs;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.format.BPFormatText;
import bp.res.BPResourceByteArray;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsBase;
import bp.ui.util.UIUtil;
import bp.web.BPWebResponse;

public class BPWebResponseContentPane extends BPTabbedPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5497809530760568516L;

	protected BPTable<String[]> m_tabheader;
	protected WebResponseTableFuncs m_headerfuncs;
	protected JPanel m_pnlcontent;
	protected Component m_ctlcontent;

	private BPWebResponse m_resp;

	public BPWebResponseContentPane()
	{
		init();
	}

	protected void init()
	{
		m_tabbar.setNoClose(true);
		m_tabheader = new BPTable<String[]>();
		m_headerfuncs = new WebResponseTableFuncs();
		m_tabheader.setMonoFont();
		m_tabheader.setModel(new BPTableModel<String[]>(m_headerfuncs));
		m_tabheader.getColumnModel().getColumn(0).setPreferredWidth((int) (120 * UIConfigs.UI_SCALE()));
		m_tabheader.getColumnModel().getColumn(1).setPreferredWidth((int) (400 * UIConfigs.UI_SCALE()));
		m_tabheader.setAutoResizeMode(BPTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane scrollheader = new JScrollPane(m_tabheader);
		scrollheader.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_pnlcontent = new JPanel();
		m_pnlcontent.setLayout(new BorderLayout());
		addTab("preview", "Content", (Icon) null, m_pnlcontent);
		addTab("header", "Header", (Icon) null, scrollheader);
	}

	public void clearResources()
	{
		m_resp = null;
		m_pnlcontent.removeAll();
		m_tabheader.clearResource();
		m_pnlcontent.validate();
		m_pnlcontent.repaint();
	}

	public BPWebResponse getWebResponse()
	{
		return m_resp;
	}

	public void setWebResponse(BPWebResponse response)
	{
		clearResources();
		if (response != null)
		{
			m_resp = response;
			Map<String, List<String>> hs = response.headerfields;
			if (hs != null)
			{
				List<String[]> headers = new ArrayList<String[]>(hs.size());
				List<String> keys = new ArrayList<String>(hs.keySet());
				keys.sort((a, b) -> (a == null ? "" : a).compareToIgnoreCase(b == null ? "" : b));
				for (String key : keys)
				{
					List<String> values = (List<String>) hs.get(key);
					StringBuilder sb = new StringBuilder();
					for (String value : values)
					{
						if (sb.length() > 0)
							sb.append(" ");
						sb.append(value);
					}
					headers.add(new String[] { key == null ? "" : key, sb.toString() });
				}
				m_tabheader.getBPTableModel().setDatas(headers);
			}
			if (response.err != null)
			{
				setError(response);
			}
			else
			{
				setContent(response);
			}
		}
		m_tabheader.refreshData();
	}

	protected void setError(BPWebResponse response)
	{
		BPCodePane txt = new BPCodePane();
		JScrollPane scroll = new JScrollPane(txt);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		txt.setMonoFont();
		txt.setText(response.err != null ? response.err.toString() : response.getText());
		txt.setCaretPosition(0);
		txt.setEditable(false);
		m_pnlcontent.add(scroll, BorderLayout.CENTER);
		m_pnlcontent.validate();
		txt.addComponentListener(new UIUtil.BPComponentListener((e) -> txt.resizeDoc(), null, null, null));
	}

	protected void setContent(BPWebResponse response)
	{
		if (response.hasContent())
		{
			String contenttype = response.getContentType();
			if (contenttype == null)
				contenttype = BPFormatText.MIME_TEXT;
			BPFormat format = BPFormatManager.getFormatByExt(contenttype);
			BPEditorFactory fac = BPEditorManager.getFactory(format.getName());
			BPEditor<?> editor = fac.createEditor(format, null, null);
			fac.initEditor(editor, format, new BPResourceByteArray(response.content.getByteArray(), null, contenttype, "", "", true), null);
			m_pnlcontent.add(editor.getComponent(), BorderLayout.CENTER);
			m_pnlcontent.validate();
			if (editor.needActiveOnStart())
				editor.activeEditor();
		}
	}

	protected static class WebResponseTableFuncs extends BPTableFuncsBase<String[]>
	{
		public WebResponseTableFuncs()
		{
			m_colnames = new String[] { "Key", "Value" };
			m_cols = new Class<?>[] { String.class, String.class };
		}

		public Object getValue(String[] o, int row, int col)
		{
			return o[col];
		}
	}
}
