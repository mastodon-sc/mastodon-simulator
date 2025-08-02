/*-
 * #%L
 * Mastodon Simulator
 * %%
 * Copyright (C) 2023 - 2025 Vladimir Ulman
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.mamut.spots.util;

import org.mastodon.mamut.ProjectModel;
import org.mastodon.mamut.model.Link;
import org.mastodon.mamut.model.Spot;
import org.mastodon.model.tag.ObjTagMap;
import org.mastodon.model.tag.TagSetModel;
import org.mastodon.model.tag.TagSetStructure;

public class CopyTagsBetweenSpots {
	private TagSetModel<Spot, Link> tagSetModel;

	public CopyTagsBetweenSpots(ProjectModel projectModel) {
		tagSetModel = projectModel.getModel().getTagSetModel();
	}


	public void deleteSpotFromAllTS(final Spot spot) {
		tagSetModel.getTagSetStructure().getTagSets().forEach(ts -> {
			ObjTagMap<Spot, TagSetStructure.Tag> tagMap = tagSetModel.getVertexTags().tags(ts);
			if (tagMap.get(spot) != null) { tagMap.remove(spot); }
		});
	}

	public void insertSpotIntoSameTSAs(final Spot spot, final Spot referenceSpot) {
		tagSetModel.getTagSetStructure().getTagSets().forEach(ts -> {
			ObjTagMap<Spot, TagSetStructure.Tag> tagMap = tagSetModel.getVertexTags().tags(ts);
			TagSetStructure.Tag tag = tagMap.get(referenceSpot);
			if (tag != null) { tagMap.set(spot,tag); }
		});
	}
}
