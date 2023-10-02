
package io.angularpay.forex.models;

import lombok.Data;

@Data
public class FixerQuery {

    private Double amount;
    private String from;
    private String to;
}
