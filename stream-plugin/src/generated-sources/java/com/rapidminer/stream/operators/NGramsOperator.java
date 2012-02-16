package com.rapidminer.stream.operators;

import com.rapidminer.stream.DataStreamOperator;
import com.rapidminer.operator.OperatorDescription;

/**
 * AUTO GENERATED wrapper Operator.
 */
public class NGramsOperator extends DataStreamOperator {

    public NGramsOperator( OperatorDescription desc ){
        super( desc, stream.data.mapper.NGrams.class );
    }
}
