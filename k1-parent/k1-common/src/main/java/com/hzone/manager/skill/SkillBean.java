package com.hzone.manager.skill;

import com.hzone.cfg.base.AbstractExcelBean;
import com.hzone.enums.EActionType;
import com.hzone.enums.battle.EBattleAction;
import com.hzone.util.StringUtil;
import com.hzone.util.excel.RowData;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 球员技能配置
 * @author zehong.he
 *
 */
public class SkillBean extends AbstractExcelBean implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int TYPE_OFFENSE = 1;
    public static final int TYPE_DEFENSE = 0;

    private int skillId;
    //0是防守，1是进攻
    private int type;
    private EBattleAction action;
    private EActionType subAction;

    private int maxLevel;
    private EActionType addType;
    private List<Float> addFloat;

    @Override
    public void initExec(RowData row) {
        int aid = row.get("actionId");
        this.action = aid < 0 ? null : EBattleAction.values()[aid];

        EActionType pact = EActionType.convertByName(getStr(row, "playerAction"));
        this.subAction = pact == null ? EActionType.NULL : pact;
        EActionType sact = EActionType.convertByName(getStr(row, "actName"));
        addType = sact == null ? EActionType.NULL : sact;

        String val = row.get("Val");
        addFloat = Lists.newArrayList();
        if (type == 2) {//技能等级附加能力
            Integer[] vals = StringUtil.toIntegerArray(val, StringUtil.DEFAULT_ST);
            for (Integer levActVal : vals) {
                addFloat.add(levActVal / 10000f);
            }
        }
    }

    public EActionType getAddType() {
        return addType;
    }

    public float getAddFloat(int level) {
        if (level <= 0 || level > addFloat.size()) {
            return 0f;
        }

        return addFloat.get(level - 1);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public EActionType getSubAction() {
        return subAction;
    }

    public EBattleAction getAction() {
        return action;
    }

    public int getSkillId() {
        return skillId;
    }

    public boolean isAttack() {
        return this.type == 1;
    }

    @Override
    public String toString() {
        return "{" +
            "\"id\":" + skillId +
            ", \"type\":" + type +
            ", \"action\":" + action +
            ", \"subact\":" + subAction +
            '}';
    }
}
