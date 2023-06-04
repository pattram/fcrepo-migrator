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

public class ObjectListEntry {

	private String pid;
	private String title;
	private String contentModel;
	private String owner;
	private String handle;
	private String lastUpdate;

	public ObjectListEntry(String pid, String title, String contentModel, String owner, String handle, String lastUpdate) {
		this.setPid(pid);
		this.setTitle(title);
		this.setContentModel(contentModel);
		this.setOwner(owner);
		this.setHandle(handle);
		this.setLastUpdate(lastUpdate);	
	}
	
	public String getPid() {
		return this.pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContentModel() {
		return this.contentModel;
	}
	public void setContentModel(String contentModel) {
		this.contentModel = contentModel;
	}
	public String getOwner() {
		return this.owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getHandle() {
		return this.handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getLastUpdate() {
		return this.lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}
