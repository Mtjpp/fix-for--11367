/*
Copyright (C) 2003 Nizar N. Batada, Morten O. Alver

All programs in this directory and
subdirectories are published under the GNU General Public License as
described below.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
USA

Further information about the GNU GPL is available at:
http://www.gnu.org/copyleft/gpl.ja.html

*/
package net.sf.jabref;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.IOException;
import java.util.Stack;
import javax.swing.event.HyperlinkListener;
import java.io.File;

public class HelpContent extends JEditorPane {

    JScrollPane pane;
    private Stack history, forw;
    JabRefPreferences prefs;
    public HelpContent(JabRefPreferences prefs_) {
	pane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        prefs = prefs_;
	history = new Stack();
	forw = new Stack();
	setContentType("text/html");
	setText("");
	setEditable(false);
    }

    public boolean back() {
	if (!history.empty()) {
	    URL prev = (URL)(history.pop());
	    forw.push(getPage());
	    setPageOnly(prev);
	}
	//System.out.println("HelpContent: "+history.empty());
	return !history.empty();
    }

    public boolean forward() {
	if (!forw.empty()) {
	    URL next = (URL)(forw.pop());
	    history.push(getPage());
	    setPageOnly(next);
	}
	return !forw.empty();


    }

    public void reset() {
	forw.removeAllElements();
	history.removeAllElements();
    }

    public void setPage(String filename) {
	String middle = prefs.get("language")+"/";
	if (middle.equals("en/")) middle = ""; // english in base help dir.
	URL old = getPage(),
	    translatedFile = JabRef.class.getResource
	    (GUIGlobals.helpPre+middle+filename);
	try {
	    super.setPage(translatedFile);
	} catch (IOException ex) {
	    //System.err.println("Could not load '"+prefs.get("language")
            //	       +"' translated version of "+filename+".");

	    URL file = /*GUIGlobals.class.*/HelpContent.class.getResource
		(GUIGlobals.helpPre+filename);

	    setPageOnly(file);
	    forw.removeAllElements();
	    if (old != null)
		history.push(old);
	    return;
	}

	forw.removeAllElements();
	if (old != null)
	    history.push(old);

    }

    public void setPage(URL url) {
      File f = new File(url.getPath());
      setPage(f.getName());
	//String lang = prefs.get("language");
	/*URL old = getPage();
	setPageOnly(url);
	forw.removeAllElements();
	if (old != null)
	    history.push(old);*/
    }

    private void setPageOnly(URL url) {
	try {
	    super.setPage(url);
	} catch (IOException ex) {
	    System.out.println("Error: could not read help file: '"
			       +url.getFile()+"'");
	}
    }

    public JComponent getPane() {
	return pane;
    }

    public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D)g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_RENDERING,
			    RenderingHints.VALUE_RENDER_QUALITY);
	super.paintComponent(g2);
    }

}
