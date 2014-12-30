package com.gmail.alikstudio47;

import static java.lang.Math.min;

public class StringUtils {
	public static int distance ( String a, String b ) {
		a = a.toLowerCase( );
		b = b.toLowerCase( );
		// i == 0
		int[] costs = new int[ b.length( ) + 1 ];
		for ( int j = 0; j < costs.length; j++ )
			costs[ j ] = j;
		for ( int i = 1; i <= a.length( ); i++ ) {
			// j == 0; nw = lev(i - 1, j)
			costs[ 0 ] = i;
			int nw = i - 1;
			for ( int j = 1; j <= b.length( ); j++ ) {
				int cj = min( 1 + min( costs[ j ], costs[ j - 1 ] ),
						a.charAt( i - 1 ) == b.charAt( j - 1 ) ? nw : nw + 1 );
				nw = costs[ j ];
				costs[ j ] = cj;
			}
		}

		return costs[ b.length( ) ];
	}

	public static String findClosestString ( String given, String[] available ) {
		String best = "";
		int min = 0;

		for ( String curr : available ) {
			int dist = StringUtils.distance( given, curr );
			if ( min == 0 || dist < min ) {
				min = dist;
				best = curr;
			}
		}

		return best;
	}

}
