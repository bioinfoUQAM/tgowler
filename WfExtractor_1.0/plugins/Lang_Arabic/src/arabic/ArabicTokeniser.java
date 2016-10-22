/*
 *  ArabicTokeniser.java
 *
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 */

package arabic;

import java.net.URL;

import gate.creole.tokeniser.DefaultTokeniser;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.CreoleParameter;

@CreoleResource(name="Arabic Tokeniser")
public class ArabicTokeniser extends DefaultTokeniser {

  private static final long serialVersionUID = 8505058866441067794L;

  @CreoleParameter(defaultValue="resources/tokeniser/arabicTokeniser.rules")
	public void setTokeniserRulesURL(URL url) {
		super.setTokeniserRulesURL(url);
	}

	@CreoleParameter(defaultValue="resources/tokeniser/postprocess.jape")
	public void setTransducerGrammarURL(URL url) {
		super.setTransducerGrammarURL(url);
	}
}
