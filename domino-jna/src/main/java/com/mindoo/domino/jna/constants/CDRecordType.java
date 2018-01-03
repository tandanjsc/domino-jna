package com.mindoo.domino.jna.constants;

import java.util.HashMap;
import java.util.Map;

import com.mindoo.domino.jna.internal.NotesConstants;

/**
 * Enum with all available CD record types, which are the building blocks of Notes Richtext.
 * 
 * @author Karsten Lehmann
 */
public enum CDRecordType {
	PDEF_MAIN(NotesConstants.SIG_CD_PDEF_MAIN),
	PDEF_TYPE(NotesConstants.SIG_CD_PDEF_TYPE),
	PDEF_PROPERTY(NotesConstants.SIG_CD_PDEF_PROPERTY),
	PDEF_ACTION(NotesConstants.SIG_CD_PDEF_ACTION),
	TABLECELL_DATAFLAGS(NotesConstants.SIG_CD_TABLECELL_DATAFLAGS),
	EMBEDDEDCONTACTLIST(NotesConstants.SIG_CD_EMBEDDEDCONTACTLIST),
	IGNORE(NotesConstants.SIG_CD_IGNORE),
	TABLECELL_HREF2(NotesConstants.SIG_CD_TABLECELL_HREF2),
	HREFBORDER(NotesConstants.SIG_CD_HREFBORDER),
	TABLEDATAEXTENSION(NotesConstants.SIG_CD_TABLEDATAEXTENSION),
	EMBEDDEDCALCTL(NotesConstants.SIG_CD_EMBEDDEDCALCTL),
	ACTIONEXT(NotesConstants.SIG_CD_ACTIONEXT),
	EVENT_LANGUAGE_ENTRY(NotesConstants.SIG_CD_EVENT_LANGUAGE_ENTRY),
	FILESEGMENT(NotesConstants.SIG_CD_FILESEGMENT),
	FILEHEADER(NotesConstants.SIG_CD_FILEHEADER),
	DATAFLAGS(NotesConstants.SIG_CD_DATAFLAGS),
	BACKGROUNDPROPERTIES(NotesConstants.SIG_CD_BACKGROUNDPROPERTIES),
	EMBEDEXTRA_INFO(NotesConstants.SIG_CD_EMBEDEXTRA_INFO),
	CLIENT_BLOBPART(NotesConstants.SIG_CD_CLIENT_BLOBPART),
	CLIENT_EVENT(NotesConstants.SIG_CD_CLIENT_EVENT),
	BORDERINFO_HS(NotesConstants.SIG_CD_BORDERINFO_HS),
	LARGE_PARAGRAPH(NotesConstants.SIG_CD_LARGE_PARAGRAPH),
	EXT_EMBEDDEDSCHED(NotesConstants.SIG_CD_EXT_EMBEDDEDSCHED),
	BOXSIZE(NotesConstants.SIG_CD_BOXSIZE),
	POSITIONING(NotesConstants.SIG_CD_POSITIONING),
	LAYER(NotesConstants.SIG_CD_LAYER),
	DECSFIELD(NotesConstants.SIG_CD_DECSFIELD),
	SPAN_END(NotesConstants.SIG_CD_SPAN_END),
	SPAN_BEGIN(NotesConstants.SIG_CD_SPAN_BEGIN),
	TEXTPROPERTIESTABLE(NotesConstants.SIG_CD_TEXTPROPERTIESTABLE),
	HREF2(NotesConstants.SIG_CD_HREF2),
	BACKGROUNDCOLOR(NotesConstants.SIG_CD_BACKGROUNDCOLOR),
	INLINE(NotesConstants.SIG_CD_INLINE),
	V6HOTSPOTBEGIN_CONTINUATION(NotesConstants.SIG_CD_V6HOTSPOTBEGIN_CONTINUATION),
	TARGET_DBLCLK(NotesConstants.SIG_CD_TARGET_DBLCLK),
	CAPTION(NotesConstants.SIG_CD_CAPTION),
	LINKCOLORS(NotesConstants.SIG_CD_LINKCOLORS),
	TABLECELL_HREF(NotesConstants.SIG_CD_TABLECELL_HREF),
	ACTIONBAREXT(NotesConstants.SIG_CD_ACTIONBAREXT),
	IDNAME(NotesConstants.SIG_CD_IDNAME),
	TABLECELL_IDNAME(NotesConstants.SIG_CD_TABLECELL_IDNAME),
	IMAGESEGMENT(NotesConstants.SIG_CD_IMAGESEGMENT),
	IMAGEHEADER(NotesConstants.SIG_CD_IMAGEHEADER),
	V5HOTSPOTBEGIN(NotesConstants.SIG_CD_V5HOTSPOTBEGIN),
	V5HOTSPOTEND(NotesConstants.SIG_CD_V5HOTSPOTEND),
	TEXTPROPERTY(NotesConstants.SIG_CD_TEXTPROPERTY),
	PARAGRAPH(NotesConstants.SIG_CD_PARAGRAPH),
	PABDEFINITION(NotesConstants.SIG_CD_PABDEFINITION),
	PABREFERENCE(NotesConstants.SIG_CD_PABREFERENCE),
	TEXT(NotesConstants.SIG_CD_TEXT),
	HEADER(NotesConstants.SIG_CD_HEADER),
	LINKEXPORT2(NotesConstants.SIG_CD_LINKEXPORT2),
	BITMAPHEADER(NotesConstants.SIG_CD_BITMAPHEADER),
	BITMAPSEGMENT(NotesConstants.SIG_CD_BITMAPSEGMENT),
	COLORTABLE(NotesConstants.SIG_CD_COLORTABLE),
	GRAPHIC(NotesConstants.SIG_CD_GRAPHIC),
	PMMETASEG(NotesConstants.SIG_CD_PMMETASEG),
	WINMETASEG(NotesConstants.SIG_CD_WINMETASEG),
	MACMETASEG(NotesConstants.SIG_CD_MACMETASEG),
	CGMMETA(NotesConstants.SIG_CD_CGMMETA),
	PMMETAHEADER(NotesConstants.SIG_CD_PMMETAHEADER),
	WINMETAHEADER(NotesConstants.SIG_CD_WINMETAHEADER),
	MACMETAHEADER(NotesConstants.SIG_CD_MACMETAHEADER),
	TABLEBEGIN(NotesConstants.SIG_CD_TABLEBEGIN),
	TABLECELL(NotesConstants.SIG_CD_TABLECELL),
	TABLEEND(NotesConstants.SIG_CD_TABLEEND),
	STYLENAME(NotesConstants.SIG_CD_STYLENAME),
	STORAGELINK(NotesConstants.SIG_CD_STORAGELINK),
	TRANSPARENTTABLE(NotesConstants.SIG_CD_TRANSPARENTTABLE),
	HORIZONTALRULE(NotesConstants.SIG_CD_HORIZONTALRULE),
	ALTTEXT(NotesConstants.SIG_CD_ALTTEXT),
	ANCHOR(NotesConstants.SIG_CD_ANCHOR),
	HTMLBEGIN(NotesConstants.SIG_CD_HTMLBEGIN),
	HTMLEND(NotesConstants.SIG_CD_HTMLEND),
	HTMLFORMULA(NotesConstants.SIG_CD_HTMLFORMULA),
	NESTEDTABLEBEGIN(NotesConstants.SIG_CD_NESTEDTABLEBEGIN),
	NESTEDTABLECELL(NotesConstants.SIG_CD_NESTEDTABLECELL),
	NESTEDTABLEEND(NotesConstants.SIG_CD_NESTEDTABLEEND),
	COLOR(NotesConstants.SIG_CD_COLOR),
	TABLECELL_COLOR(NotesConstants.SIG_CD_TABLECELL_COLOR),

	BLOBPART(NotesConstants.SIG_CD_BLOBPART),
	BEGIN(NotesConstants.SIG_CD_BEGIN),
	END(NotesConstants.SIG_CD_END),
	VERTICALALIGN(NotesConstants.SIG_CD_VERTICALALIGN),
	FLOATPOSITION(NotesConstants.SIG_CD_FLOATPOSITION),
	TIMERINFO(NotesConstants.SIG_CD_TIMERINFO),
	TABLEROWHEIGHT(NotesConstants.SIG_CD_TABLEROWHEIGHT),
	TABLELABEL(NotesConstants.SIG_CD_TABLELABEL),
	BIDI_TEXT(NotesConstants.SIG_CD_BIDI_TEXT),
	BIDI_TEXTEFFECT(NotesConstants.SIG_CD_BIDI_TEXTEFFECT),
	REGIONBEGIN(NotesConstants.SIG_CD_REGIONBEGIN),
	REGIONEND(NotesConstants.SIG_CD_REGIONEND),
	TRANSITION(NotesConstants.SIG_CD_TRANSITION),
	FIELDHINT(NotesConstants.SIG_CD_FIELDHINT),
	PLACEHOLDER(NotesConstants.SIG_CD_PLACEHOLDER),
	EMBEDDEDOUTLINE(NotesConstants.SIG_CD_EMBEDDEDOUTLINE),
	EMBEDDEDVIEW(NotesConstants.SIG_CD_EMBEDDEDVIEW),
	CELLBACKGROUNDDATA(NotesConstants.SIG_CD_CELLBACKGROUNDDATA),

	/* Signatures for Frameset CD records */
	FRAMESETHEADER(NotesConstants.SIG_CD_FRAMESETHEADER),
	FRAMESET(NotesConstants.SIG_CD_FRAMESET),
	FRAME(NotesConstants.SIG_CD_FRAME),
	/* Signature for Target Frame info on a link	*/
	TARGET(NotesConstants.SIG_CD_TARGET),
	MAPELEMENT(NotesConstants.SIG_CD_MAPELEMENT),
	AREAELEMENT(NotesConstants.SIG_CD_AREAELEMENT),
	HREF(NotesConstants.SIG_CD_HREF),
	EMBEDDEDCTL(NotesConstants.SIG_CD_EMBEDDEDCTL),
	HTML_ALTTEXT(NotesConstants.SIG_CD_HTML_ALTTEXT),
	EVENT(NotesConstants.SIG_CD_EVENT),
	PRETABLEBEGIN(NotesConstants.SIG_CD_PRETABLEBEGIN),
	BORDERINFO(NotesConstants.SIG_CD_BORDERINFO),
	EMBEDDEDSCHEDCTL(NotesConstants.SIG_CD_EMBEDDEDSCHEDCTL),
	EXT2_FIELD(NotesConstants.SIG_CD_EXT2_FIELD),
	EMBEDDEDEDITCTL(NotesConstants.SIG_CD_EMBEDDEDEDITCTL),

	/* Signatures for Composite Records that are reserved internal records, */
	/* whose format may change between releases. */
	DOCUMENT_PRE_26(NotesConstants.SIG_CD_DOCUMENT_PRE_26),
	FIELD_PRE_36(NotesConstants.SIG_CD_FIELD_PRE_36),
	FIELD(NotesConstants.SIG_CD_FIELD),
	DOCUMENT(NotesConstants.SIG_CD_DOCUMENT),
	METAFILE(NotesConstants.SIG_CD_METAFILE),
	BITMAP(NotesConstants.SIG_CD_BITMAP),
	FONTTABLE(NotesConstants.SIG_CD_FONTTABLE),
	LINK(NotesConstants.SIG_CD_LINK),
	LINKEXPORT(NotesConstants.SIG_CD_LINKEXPORT),
	KEYWORD(NotesConstants.SIG_CD_KEYWORD),
	LINK2(NotesConstants.SIG_CD_LINK2),
	CGM(NotesConstants.SIG_CD_CGM),
	TIFF(NotesConstants.SIG_CD_TIFF),
	PATTERNTABLE(NotesConstants.SIG_CD_PATTERNTABLE),
	DDEBEGIN(NotesConstants.SIG_CD_DDEBEGIN),
	DDEEND(NotesConstants.SIG_CD_DDEEND),
	OLEBEGIN(NotesConstants.SIG_CD_OLEBEGIN),
	OLEEND(NotesConstants.SIG_CD_OLEEND),
	HOTSPOTBEGIN(NotesConstants.SIG_CD_HOTSPOTBEGIN),
	HOTSPOTEND(NotesConstants.SIG_CD_HOTSPOTEND),
	BUTTON(NotesConstants.SIG_CD_BUTTON),
	BAR(NotesConstants.SIG_CD_BAR),
	V4HOTSPOTBEGIN(NotesConstants.SIG_CD_V4HOTSPOTBEGIN),
	V4HOTSPOTEND(NotesConstants.SIG_CD_V4HOTSPOTEND),
	EXT_FIELD(NotesConstants.SIG_CD_EXT_FIELD),
	LSOBJECT(NotesConstants.SIG_CD_LSOBJECT),
	HTMLHEADER(NotesConstants.SIG_CD_HTMLHEADER),
	HTMLSEGMENT(NotesConstants.SIG_CD_HTMLSEGMENT),
	LAYOUT(NotesConstants.SIG_CD_LAYOUT),
	LAYOUTTEXT(NotesConstants.SIG_CD_LAYOUTTEXT),
	LAYOUTEND(NotesConstants.SIG_CD_LAYOUTEND),
	LAYOUTFIELD(NotesConstants.SIG_CD_LAYOUTFIELD),
	PABHIDE(NotesConstants.SIG_CD_PABHIDE),
	PABFORMREF(NotesConstants.SIG_CD_PABFORMREF),
	ACTIONBAR(NotesConstants.SIG_CD_ACTIONBAR),
	ACTION(NotesConstants.SIG_CD_ACTION),
	DOCAUTOLAUNCH(NotesConstants.SIG_CD_DOCAUTOLAUNCH),
	LAYOUTGRAPHIC(NotesConstants.SIG_CD_LAYOUTGRAPHIC),
	OLEOBJINFO(NotesConstants.SIG_CD_OLEOBJINFO),
	LAYOUTBUTTON(NotesConstants.SIG_CD_LAYOUTBUTTON),
	TEXTEFFECT(NotesConstants.SIG_CD_TEXTEFFECT),

	/* Signatures for items of type TYPE_VIEWMAP */
	VMHEADER(NotesConstants.SIG_CD_VMHEADER),
	VMBITMAP(NotesConstants.SIG_CD_VMBITMAP),
	VMRECT(NotesConstants.SIG_CD_VMRECT),
	VMPOLYGON_BYTE(NotesConstants.SIG_CD_VMPOLYGON_BYTE),
	VMPOLYLINE_BYTE(NotesConstants.SIG_CD_VMPOLYLINE_BYTE),
	VMREGION(NotesConstants.SIG_CD_VMREGION),
	VMACTION(NotesConstants.SIG_CD_VMACTION),
	VMELLIPSE(NotesConstants.SIG_CD_VMELLIPSE),
	VMRNDRECT(NotesConstants.SIG_CD_VMRNDRECT),
	VMBUTTON(NotesConstants.SIG_CD_VMBUTTON),
	VMACTION_2(NotesConstants.SIG_CD_VMACTION_2),
	VMTEXTBOX(NotesConstants.SIG_CD_VMTEXTBOX),
	VMPOLYGON(NotesConstants.SIG_CD_VMPOLYGON),
	VMPOLYLINE(NotesConstants.SIG_CD_VMPOLYLINE),
	VMPOLYRGN(NotesConstants.SIG_CD_VMPOLYRGN),
	VMCIRCLE(NotesConstants.SIG_CD_VMCIRCLE),
	VMPOLYRGN_BYTE(NotesConstants.SIG_CD_VMPOLYRGN_BYTE),

	/* Signatures for alternate CD sequences*/
	ALTERNATEBEGIN(NotesConstants.SIG_CD_ALTERNATEBEGIN),
	ALTERNATEEND(NotesConstants.SIG_CD_ALTERNATEEND),
	OLERTMARKER(NotesConstants.SIG_CD_OLERTMARKER);
	
	private static Map<Short,CDRecordType> m_recordsByConstant;
	static {
		m_recordsByConstant = new HashMap<Short, CDRecordType>();
		for (CDRecordType currType : values()) {
			m_recordsByConstant.put(currType.getConstant(), currType);
		}
	}
	private short m_val;
	
	CDRecordType(short val) {
		m_val = val;
	}
	
	public short getConstant() {
		return m_val;
	}
	
	public static CDRecordType getRecordForConstant(short constant) {
		return m_recordsByConstant.get(constant);
	}
}
