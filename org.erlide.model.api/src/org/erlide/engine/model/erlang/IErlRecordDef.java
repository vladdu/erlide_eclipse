/**
 *
 */
package org.erlide.engine.model.erlang;

/**
 * @author jakob
 *
 */
public interface IErlRecordDef extends IErlPreprocessorDef {

    IErlRecordField getFieldNamed(String name);

}
