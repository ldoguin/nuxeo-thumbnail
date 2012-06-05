/*
 * Copyright (c) 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * ldoguin
 * 
 */
package org.nuxeo.thumb.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.EventServiceAdmin;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.thumb.Thumbnail;
import org.nuxeo.thumb.ThumbnailConstants;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(type = BackendType.H2, user = "Administrator")
@Deploy({ "nuxeo-thumbnail", "org.nuxeo.ecm.core.convert",
		"org.nuxeo.ecm.platform.commandline.executor",
		"org.nuxeo.ecm.platform.dublincore",
		"org.nuxeo.ecm.platform.picture.core",
		"org.nuxeo.ecm.platform.picture.api",
		"org.nuxeo.ecm.platform.picture.convert",
		"org.nuxeo.ecm.platform.filemanager.core",
		"org.nuxeo.ecm.platform.filemanager.api" })
public class TestThumbnail {

	@Inject
	CoreSession session;

	@Test
	public void testMerge() throws Exception {
		DocumentModel doc = session.createDocumentModel("/", "file", "File");
		File file = FileUtils.getResourceFileFromContext("data/hello.pdf");
		Blob blob = new FileBlob(file);
		doc.setPropertyValue("file:content", (Serializable) blob);
		doc = session.createDocument(doc);
		session.save();
        waitForAsyncCompletion();
        doc = session.getDocument(doc.getRef());
		Thumbnail thumb = doc.getAdapter(Thumbnail.class);
		assertNotNull(thumb);
		String firstBlobDigest = thumb.getDigest();
		Blob thumbnail = thumb.getThumbnail();
		assertNotNull(thumbnail);
		doc = session.getDocument(doc.getRef());
		assertTrue(doc.hasFacet(ThumbnailConstants.THUMBNAIL_FACET));

		// change content
		file = FileUtils.getResourceFileFromContext("data/testFile.pdf");
		blob = new FileBlob(file);
		doc.setPropertyValue("file:content", (Serializable) blob);
		doc = session.saveDocument(doc);
		session.save();
        waitForAsyncCompletion();
        doc = session.getDocument(doc.getRef());
		thumb = doc.getAdapter(Thumbnail.class);
		String secondBlobDigest = thumb.getDigest();
		assertNotSame(firstBlobDigest, secondBlobDigest);

		// remove content
		doc.setPropertyValue("file:content", null);
		doc = session.saveDocument(doc);
		session.save();
        waitForAsyncCompletion();
        doc = session.getDocument(doc.getRef());
		assertFalse(doc.hasFacet(ThumbnailConstants.THUMBNAIL_FACET));

	}

	private void waitForAsyncCompletion() throws ClientException {
        Framework.getLocalService(EventService.class).waitForAsyncCompletion();
        session.save(); 
	}
}
