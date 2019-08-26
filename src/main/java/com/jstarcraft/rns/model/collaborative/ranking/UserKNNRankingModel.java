package com.jstarcraft.rns.model.collaborative.ranking;

import java.util.Iterator;

import com.jstarcraft.ai.data.DataInstance;
import com.jstarcraft.ai.math.structure.vector.SparseVector;
import com.jstarcraft.ai.math.structure.vector.VectorScalar;
import com.jstarcraft.rns.model.collaborative.UserKNNModel;

/**
 * 
 * User KNN推荐器
 * 
 * <pre>
 * 参考LibRec团队
 * </pre>
 * 
 * @author Birdy
 *
 */
public class UserKNNRankingModel extends UserKNNModel {

    @Override
    public void predict(DataInstance instance) {
        int userIndex = instance.getQualityFeature(userDimension);
        int itemIndex = instance.getQualityFeature(itemDimension);
        SparseVector itemVector = itemVectors[itemIndex];
        int[] neighbors = userNeighbors[userIndex];
        if (itemVector.getElementSize() == 0 || neighbors == null) {
            instance.setQuantityMark(0F);
            return;
        }

        float sum = 0F, absolute = 0F;
        int count = 0;
        int leftCursor = 0, rightCursor = 0, leftSize = itemVector.getElementSize(), rightSize = neighbors.length;
        Iterator<VectorScalar> iterator = itemVector.iterator();
        VectorScalar term = iterator.next();
        // 判断两个有序数组中是否存在相同的数字
        while (leftCursor < leftSize && rightCursor < rightSize) {
            if (term.getIndex() == neighbors[rightCursor]) {
                count++;
                sum += similarityMatrix.getValue(userIndex, neighbors[rightCursor]);
                if (iterator.hasNext()) {
                    term = iterator.next();
                }
                leftCursor++;
                rightCursor++;
            } else if (term.getIndex() > neighbors[rightCursor]) {
                rightCursor++;
            } else if (term.getIndex() < neighbors[rightCursor]) {
                if (iterator.hasNext()) {
                    term = iterator.next();
                }
                leftCursor++;
            }
        }

        if (count == 0) {
            instance.setQuantityMark(0F);
            return;
        }

        instance.setQuantityMark(sum);
    }

}
