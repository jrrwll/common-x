package org.dreamcat.common.excel.parse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Create by tuke on 2020/8/27
 * <p>
 * ----.-----.-----      ----.-----.-----      ----.-----.-----      ----.-----.-----
 * vector                dynamic              vector[]             dynamic[]
 * ----.-----.-----      ----.-----.-----      ----.-----.-----      ----.-----.-----
 * scalar scalar scalar  scalar scalar scalar
 * scalar scalar scalar scalar scalar scalar  scalar scalar scalar  scalar scalar scalar
 * scalar scalar scalar  scalar scalar scalar
 * ----.-----.-----      ----.-----.-----     ----.-----.-----      ----.-----.-----
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SVRow<S, V> extends SVColumn<S> {

    public List<SVColumn<V>> vector;
}
