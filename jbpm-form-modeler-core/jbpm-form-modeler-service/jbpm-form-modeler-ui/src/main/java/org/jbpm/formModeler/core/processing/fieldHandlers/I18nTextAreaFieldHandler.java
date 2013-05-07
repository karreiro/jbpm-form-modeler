/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.processing.fieldHandlers;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.i18n.I18nEntry;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.annotation.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handler for I18nTextArea
 */
public class I18nTextAreaFieldHandler extends DefaultFieldHandler {

    public boolean isEmpty(Object value) {
        if (value == null || ((I18nSet) value).isEmpty()) {
            return true;
        }
        if ("".equals(((I18nSet) value).getValue(LocaleManager.lookup().getDefaultLang()))) {
            return true;
        }
        for (Iterator it = ((I18nSet) value).iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            if (entry.getValue() != null && !"".equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine the list of class types this field can generate. That is, normally,
     * a field can generate multiple outputs (an input text can generate Strings,
     * Integers, ...)
     *
     * @return the set of class types that can be generated by this handler.
     */
    public String[] getCompatibleClassNames() {
        return new String[]{"I18nText", "I18nTextArea"};
    }

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        I18nSet set = new I18nSet();
        for (Iterator it = parametersMap.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (key.length() > inputName.length() && key.startsWith(inputName)) {
                String lang = key.substring(inputName.length() + 1);
                String value = ((String[]) parametersMap.get(key))[0];
                set.setValue(lang, value);
            }
        }
        return set.isEmpty() ? null : set;
    }

    /**
     * Determine the value as a parameter map for a given input value. This is like the inverse operation of getValue()
     *
     * @param objectValue Object value to represent
     * @param pattern     Pattern to apply if any
     * @return a Map representing the parameter values expected inside a request that would cause the form
     *         to generate given object value as a result.
     */
    public Map getParamValue(String inputName, Object objectValue, String pattern) {
        if (objectValue == null)  return Collections.EMPTY_MAP;
        Map m = new HashMap();
        I18nSet value = (I18nSet) objectValue;
        for (Iterator it = value.iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            m.put(inputName + "_" + entry.getLang(), new String[]{(String) entry.getValue()});
        }
        m.put(inputName, new String[]{});
        return m;
    }

    public boolean acceptsPropertyName(String propName) {
        return true;
    }
}