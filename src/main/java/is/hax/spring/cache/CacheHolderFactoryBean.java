/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package is.hax.spring.cache;

import org.springframework.beans.factory.FactoryBean;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Map;
import java.util.Collections;


/**
 * Simple wrapper to expose a Cache in the Spring context.
 * 
 * @author Peter Backlund
 * Borrowed from http://code.google.com/p/feeling-lucky-pictures
 */
public class CacheHolderFactoryBean implements FactoryBean {

	Map cacheProperties = Collections.emptyMap();

	public void setCacheProperties(Map cacheProperties) {
		this.cacheProperties = cacheProperties;
	}

	public Object getObject() throws Exception {
		Cache cache = CacheManager
                .getInstance()
                .getCacheFactory()
                .createCache(cacheProperties);

        return new CacheHolder(cache);
	}

	public Class<CacheHolder> getObjectType() {
		return CacheHolder.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
