package com.lvwj.halo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.*;

/**
 *
 * @author lvweijie
 * @date 2023年11月10日 17:17
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VersionEntity extends DeleteEntity {

    @Version
    private Long version;
}
