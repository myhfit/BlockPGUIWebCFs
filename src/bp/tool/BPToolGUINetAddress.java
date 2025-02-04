package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.NetworkUtil;
import bp.util.Std;
import bp.util.NetworkUtil.NetworkSendResult;

public class BPToolGUINetAddress extends BPToolGUIBase<BPToolGUINetAddress.BPToolGUIContextNetAddress>
{
	public String getName()
	{
		return "Address Info";
	}

	protected BPToolGUIContextNetAddress createToolContext()
	{
		return new BPToolGUIContextNetAddress();
	}

	protected static class BPToolGUIContextNetAddress implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPTextField m_txtaddr;
		protected BPList<InetAddress> m_lstaddrs;
		protected List<InetAddress> m_addrs;
		protected BPTextPane m_txtinfo;

		public void initUI(Container par, Object... params)
		{
			JPanel lp = new JPanel();
			JPanel rp = new JPanel();
			BPToolBarSQ cmdbar = new BPToolBarSQ();
			m_txtaddr = new BPTextField();
			m_lstaddrs = new BPList<InetAddress>();
			m_txtinfo = new BPTextPane();
			m_txtinfo.setBorder(new EmptyBorder(0, 0, 0, 0));
			JScrollPane scrollp = new JScrollPane();
			JScrollPane scrollr = new JScrollPane();
			BPAction acthost = BPAction.build("gethost").tooltip("Get Hostname").vIcon(BPIconResV.DROPDOWN()).callback(this::onGetHost).getAction();
			BPAction actping = BPAction.build("ping").tooltip("Send Ping").vIcon(BPIconResV.START()).callback(this::onSendPing).getAction();
			scrollp.setViewportView(m_lstaddrs);
			scrollr.setViewportView(m_txtinfo);

			cmdbar.setActions(new Action[] { BPAction.separator(), acthost, actping });
			cmdbar.setBarHeight(18);

			m_txtaddr.setMonoFont();
			m_txtinfo.setMonoFont();
			m_lstaddrs.setMonoFont();
			m_txtinfo.setEditable(false);

			scrollp.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
			lp.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			cmdbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			scrollr.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_lstaddrs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			lp.setLayout(new BorderLayout());
			rp.setLayout(new BorderLayout());

			lp.add(m_txtaddr, BorderLayout.NORTH);
			lp.add(scrollp, BorderLayout.CENTER);
			rp.add(cmdbar, BorderLayout.NORTH);
			rp.add(scrollr, BorderLayout.CENTER);
			par.add(lp, BorderLayout.WEST);
			par.add(rp, BorderLayout.CENTER);

			m_txtaddr.addKeyListener(new UIUtil.BPKeyListener(null, this::onAddrDown, null));
			m_lstaddrs.addListSelectionListener(this::onAddressChanged);
		}

		protected void onGetHost(ActionEvent e)
		{
			InetAddress addr = m_lstaddrs.getSelectedValue();
			if (addr != null)
			{
				String host = addr.getHostName();
				m_txtinfo.setText(m_txtinfo.getText() + "\n" + host);
			}
		}

		protected void onSendPing(ActionEvent e)
		{
			InetAddress addr = m_lstaddrs.getSelectedValue();
			if (addr != null)
			{
				NetworkSendResult r = NetworkUtil.sendPing(addr, 10000, false);
				if (r.success)
					m_txtinfo.setText(m_txtinfo.getText() + "\nTime:" + r.time + "ms");
				else
					m_txtinfo.setText(m_txtinfo.getText() + "\nUnreachable");
			}
		}

		protected void onAddressChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				InetAddress addr = m_lstaddrs.getSelectedValue();
				if (addr != null)
				{
					showAddress(addr);
				}
			}
		}

		protected void showAddress(InetAddress addr)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("address  :" + addr.getHostAddress() + "\n");
			sb.append("\n");

			sb.append("local    :" + addr.isAnyLocalAddress() + "\n");
			sb.append("linklocal:" + addr.isLinkLocalAddress() + "\n");
			sb.append("loopback :" + addr.isLoopbackAddress() + "\n");
			sb.append("sitelocal:" + addr.isSiteLocalAddress() + "\n");
			sb.append("\n");
			sb.append("multicast:" + addr.isMulticastAddress() + "\n");
			sb.append("global   :" + addr.isMCGlobal() + "\n");
			sb.append("linklocal:" + addr.isMCLinkLocal() + "\n");
			sb.append("nodelocal:" + addr.isMCNodeLocal() + "\n");
			sb.append("orglocal :" + addr.isMCOrgLocal() + "\n");
			sb.append("sitelocal:" + addr.isMCSiteLocal() + "\n");

			m_txtinfo.setText(sb.toString());
		}

		protected void onAddrDown(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				String addrstr = m_txtaddr.getText().trim();
				try
				{
					InetAddress addr = InetAddress.getByName(addrstr);
					if (addr != null)
					{
						m_addrs.add(0, addr);
						m_lstaddrs.setSelectedIndex(0);
						m_lstaddrs.updateUI();
						m_txtaddr.setText("");
					}
				}
				catch (UnknownHostException e1)
				{
					UIStd.err(e1);
				}
			}
		}

		public void initDatas(Object... params)
		{
			List<InetAddress> addrs = new ArrayList<InetAddress>();

			try
			{
				for (Enumeration<NetworkInterface> ifcen = NetworkInterface.getNetworkInterfaces(); ifcen.hasMoreElements();)
				{
					NetworkInterface ifc = ifcen.nextElement();
					for (Enumeration<InetAddress> addren = ifc.getInetAddresses(); addren.hasMoreElements();)
					{
						InetAddress addr = addren.nextElement();
						addrs.add(addr);
					}
				}
			}
			catch (SocketException se)
			{
				Std.err(se);
			}
			m_addrs = addrs;

			BPListModel<InetAddress> model = new BPListModel<InetAddress>();
			model.setDatas(addrs);
			m_lstaddrs.setModel(model);
		}
	}
}
