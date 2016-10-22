/*
 *  ArabicGazCollector.java
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

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;

import java.net.URL;
import java.util.List;

@CreoleResource(name = "Arabic Gazetteer Collector", autoinstances = @AutoInstance)
public class ArabicGazCollector extends PackagedController {

  private static final long serialVersionUID = 137618834835310547L;

  @Override
  @CreoleParameter(defaultValue = "resources/arabic_lists_collector.gapp")
  public void setPipelineURL(URL url) {
    this.url = url;
  }

  @Override
  @CreoleParameter(defaultValue = "Arabic")
  public void setMenu(List<String> menu) {
    super.setMenu(menu);
  }
}
