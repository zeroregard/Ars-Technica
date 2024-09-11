package net.mcreator.ars_technica.client.particles;

import com.hollingsworth.arsnouveau.api.particle.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.events.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class SpiralDustParticleTypeData implements ParticleOptions {

    protected ParticleType<? extends SpiralDustParticleTypeData> type;
    public static final Codec<SpiralDustParticleTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
                    Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
                    Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
                    Codec.BOOL.fieldOf("disableDepthTest").forGetter(d -> d.disableDepthTest),
                    Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                    Codec.FLOAT.fieldOf("alpha").forGetter(d -> d.alpha),
                    Codec.INT.fieldOf("age").forGetter(d -> d.age)
            )
            .apply(instance, SpiralDustParticleTypeData::new));

    public ParticleColor color;
    public boolean disableDepthTest;
    public float size = .25f;
    public float alpha = 1.0f;
    public int age = 36;

    static final ParticleOptions.Deserializer<SpiralDustParticleTypeData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public SpiralDustParticleTypeData fromCommand(ParticleType<SpiralDustParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new SpiralDustParticleTypeData(type, ParticleColor.fromString(reader.readString()), reader.readBoolean());
        }

        @Override
        public SpiralDustParticleTypeData fromNetwork(ParticleType<SpiralDustParticleTypeData> type, FriendlyByteBuf buffer) {
            return new SpiralDustParticleTypeData(type, ParticleColorRegistry.from(buffer.readNbt()), buffer.readBoolean());
        }
    };

    public SpiralDustParticleTypeData(float r, float g, float b, boolean disableDepthTest, float size, float alpha, int age) {
        this(ModParticles.SPIRAL_DUST_TYPE.get(), new ParticleColor(r, g, b), disableDepthTest, size, alpha, age);
    }

    public SpiralDustParticleTypeData(ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        this(ModParticles.SPIRAL_DUST_TYPE.get(), color, disableDepthTest, size, alpha, age);
    }

    public SpiralDustParticleTypeData(ParticleType<? extends SpiralDustParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest) {
        this(particleTypeData, color, disableDepthTest, 0.1f, 1.0f, 80);
    }

    public SpiralDustParticleTypeData(ParticleType<? extends SpiralDustParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        this.type = particleTypeData;
        this.color = color;
        this.disableDepthTest = disableDepthTest;
        this.size = size;
        this.alpha = alpha;
        this.age = age;
    }


    @Override
    public ParticleType<? extends SpiralDustParticleTypeData> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeNbt(color.serialize());
    }

    @Override
    public String writeToString() {
        return getRegistryName(type).toString() + " " + color.serialize();
    }
}
