package com.gmail.alikstudio47;

import java.io.File;

public class i18n {
	private NEXTparkour plugin;
	private File folder;
	private File lang;

	public i18n( NEXTparkour plugin ) {
		this.plugin = plugin;

		String path = plugin.getDataFolder( ).getPath( ) + "\\languages\\";
		path = path.replaceAll( "\\", File.separator );

		folder = new File( path );
		folder.mkdirs( );
	}

	public i18n( NEXTparkour plugin, String _lang ) {
		this.plugin = plugin;

		String path = plugin.getDataFolder( ).getPath( ) + "\\languages\\";
		path = path.replaceAll( "\\", File.separator );

		folder = new File( path );
		folder.mkdirs( );

		path += _lang;
		path.replaceAll( "\\", File.separator );

		lang = new File( path );

		if ( !lang.exists( ) ) {
			path = path.substring( 0, _lang.length( ) );
			path += "en_EN";

			lang = new File( path );
		}
	}
}
