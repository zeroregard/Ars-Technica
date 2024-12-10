package net.mcreator.ars_technica.common.entity.fusion;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllArcaneFusionTypes {
    private static final Map<String, ArcaneFusionType> REGISTRY = new HashMap<>();

    public static final RegularType REGULAR = register(new RegularType());
    public static final HeatedType HEATED = register(new HeatedType());
    public static final SuperType SUPER = register(new SuperType());

    private static <T extends ArcaneFusionType> T register(T type) {
        REGISTRY.put(type.getId(), type);
        return type;
    }

    public static ArcaneFusionType getTypeFromId(String id) {
        return REGISTRY.get(id);
    }

    public static class RegularType implements ArcaneFusionType {
        private RegularType() {};
        @Override
        public String getTextureLocation() {
            return "textures/entity/arcane_fusion_regular.png";
        }

        @Override
        public String getId() {
            return "regular";
        }

        @Override
        public HeatCondition getSuppliedHeat() {
            return HeatCondition.NONE;
        }

        @Override
        public List<ParticleColor> getParticleColors() {
            return List.of(
                    new ParticleColor(138, 47, 161),
                    new ParticleColor(34, 22, 73),
                    new ParticleColor(206, 88, 202)
            );
        }
    }

    public static class HeatedType implements ArcaneFusionType {
        private HeatedType() {};
        @Override
        public String getTextureLocation() {
            return "textures/entity/arcane_fusion_heated.png";
        }

        @Override
        public String getId() {
            return "heated";
        }

        @Override
        public HeatCondition getSuppliedHeat() {
            return HeatCondition.HEATED;
        }

        @Override
        public List<ParticleColor> getParticleColors() {
            return List.of(
                    new ParticleColor(95, 2, 1),
                    new ParticleColor(209, 120, 0),
                    new ParticleColor(255, 213, 40)
            );
        }
    }

    public static class SuperType implements ArcaneFusionType {
        private SuperType() {};
        @Override
        public String getTextureLocation() {
            return "textures/entity/arcane_fusion_super.png";
        }

        @Override
        public String getId() {
            return "super";
        }


        @Override
        public HeatCondition getSuppliedHeat() {
            return HeatCondition.SUPERHEATED;
        }

        @Override
        public List<ParticleColor> getParticleColors() {
            return List.of(
                    new ParticleColor(62, 49, 109),
                    new ParticleColor(75, 125, 234),
                    new ParticleColor(100, 201, 253)
            );
        }
    }
}
