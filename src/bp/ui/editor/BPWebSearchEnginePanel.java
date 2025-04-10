package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.client.BPClientManager;
import bp.client.BPClientWebSearchEngine;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.format.BPFormat;
import bp.format.BPFormatUnknown;
import bp.res.BPResource;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPTable.BPTableRendererMultiline;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPToolSQButton;
import bp.ui.table.BPTableFuncsMapMethod;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;

public class BPWebSearchEnginePanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8836315447406028912L;

	protected BPTextField m_txtkw;
	protected BPTable<Map<String, Object>> m_tabresult;
	protected BPTableFuncsWebSearchResult m_resultfuncs;
	protected BPComboBox<String> m_cmbses;
	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;

	public BPWebSearchEnginePanel()
	{
		initUIComponents();
		initDatas();
	}

	protected void initUIComponents()
	{
		JPanel sp = new JPanel();
		BPToolBarSQ toolbar = new BPToolBarSQ();
		m_tabresult = new BPTable<Map<String, Object>>();
		m_resultfuncs = new BPTableFuncsWebSearchResult();
		BPTableModel<Map<String, Object>> model = new BPTableModel<Map<String, Object>>(m_resultfuncs);
		m_txtkw = new BPTextField();
		m_cmbses = new BPComboBox<String>();
		Action actrun = BPAction.build("Search").callback(this::onSearch).getAction();
		BPToolSQButton btnsearch = new BPToolSQButton(" Search ", actrun);
		JScrollPane scroll = new JScrollPane();

		m_txtkw.setLabelFont();
		m_txtkw.setBorder(new CompoundBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_TEXTQUARTER()), new EmptyBorder(0, 1, 0, 1)));
		toolbar.setBarHeight(UIConfigs.TEXTFIELD_HEIGHT() + 4);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_resultfuncs.setColumns(new String[] { "titlecontent", "url", "date" }, null);
		m_resultfuncs.setColumnMethod(0, (o) -> o.get("title") + "<br/><br/>" + o.get("content"));
		m_cmbses.setListFont();
		m_cmbses.replaceWBorder();

		m_tabresult.setTableFont();
		m_tabresult.setModel(model);
		// m_tabresult.getColumnModel().getColumn(0).setCellRenderer(new
		// BPTableRendererMultiline());
		BPTableRendererMultiline render1 = new BPTableRendererMultiline();
		render1.setDecideRowHeight(true);
		m_tabresult.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		m_tabresult.getColumnModel().getColumn(0).setCellRenderer(render1);
		m_tabresult.getColumnModel().getColumn(0).setPreferredWidth(600);
		m_tabresult.getColumnModel().getColumn(1).setPreferredWidth(264);
		m_tabresult.getColumnModel().getColumn(2).setPreferredWidth(100);

		toolbar.add(btnsearch);
		toolbar.add(Box.createRigidArea(new Dimension(1, 1)));
		toolbar.add(m_txtkw);
		toolbar.add(m_cmbses);
		toolbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));

		setLayout(new BorderLayout());
		sp.setLayout(new BorderLayout());
		scroll.setViewportView(m_tabresult);
		sp.add(scroll, BorderLayout.CENTER);
		add(sp, BorderLayout.CENTER);
		add(toolbar, BorderLayout.NORTH);

		m_txtkw.addKeyListener(new UIUtil.BPKeyListener(null, this::onKWKeyDown, null));
	}

	protected void onKWKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			e.consume();
			onSearch(null);
		}
	}

	protected void initDatas()
	{
		List<String> ses = BPClientManager.listNameByCategory(BPClientWebSearchEngine.CATEGORY_WEBSEARCHENGINE);
		BPComboBoxModel<String> model = new BPComboBoxModel<String>();
		model.setDatas(ses);
		m_cmbses.setModel(model);
		if (ses.size() > 0)
			m_cmbses.setSelectedIndex(0);
	}

	protected void onSearch(ActionEvent e)
	{
		String keyword = m_txtkw.getText();
		if (keyword == null || keyword.trim().isEmpty())
			return;
		if (m_cmbses.getSelectedIndex() == -1)
			return;
		String enginename = (String) m_cmbses.getSelectedItem();
		List<Map<String, Object>> results = UIUtil.block(() -> CompletableFuture.supplyAsync(() ->
		{
			BPClientWebSearchEngine client = BPClientManager.get(enginename, null);
			return client.searchHTML(keyword);
		}), "Query from Service...");
		BPTableModel<Map<String, Object>> model = m_tabresult.getBPTableModel();
		model.setDatas(results);
		model.fireTableDataChanged();
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	public void save()
	{
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return m_needsave;
	}

	public void setNeedSave(boolean needsave)
	{
		m_needsave = needsave;
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public String getEditorName()
	{
		return "Web Search Engine";
	}

	public static class BPTableFuncsWebSearchResult extends BPTableFuncsMapMethod
	{
		public List<Action> getActions(BPTable<Map<String, Object>> table, List<Map<String, Object>> datas, int[] rows, int r, int c)
		{
			List<Action> rc = new ArrayList<Action>();
			Action actopen = BPAction.build("Open").callback((e) ->
			{
				open(datas);
			}).getAction();
			rc.add(actopen);
			return rc;
		}

		public void open(List<Map<String, Object>> datas)
		{
			for (Map<String, Object> data : datas)
			{
				try
				{
					URI uri = new URI((String) data.get("url"));
					CommonUIOperations.openExternal(uri);
				}
				catch (URISyntaxException e)
				{
					UIStd.err(e);
				}
			}
		}
	}

	public final static class BPEditorFactoryWebSearchEnginePanel implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatUnknown.FORMAT_NA };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			BPWebSearchEnginePanel rc = new BPWebSearchEnginePanel();
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
		}

		public String getName()
		{
			return "Web SearchEngine Panel";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}
}
