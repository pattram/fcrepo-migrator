/*
 * ZIM: Center for Information Modelling, University Graz
 * 
 * Licensed to ZIM under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * ZIM licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.emile.cirilo.models;

/**
 * @author Johannes Stigler
 */

public class DatastreamListEntry {

	private String dsid;
	private String label;
	private String mimetype;
	private String controlgroup;
	private String location;
	
	public DatastreamListEntry(String dsid, String label, String mimetype, String controlgroup, String location) {
		this.setDsid(dsid);
		this.setLabel(label);
		this.setMimetype(mimetype);
		this.setControlgroup(controlgroup);
		this.setLocation(location);
	}
	
	public String getDsid() {
		return this.dsid;
	}
	public void setDsid(String dsid) {
		this.dsid = dsid;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getMimetype() {
		return this.mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getControlgroup() {
		return this.controlgroup;
	}
	public void setControlgroup(String controlgroup) {
		this.controlgroup = controlgroup;
	}
	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

}
