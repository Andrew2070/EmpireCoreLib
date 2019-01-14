package EmpireCoreLib.Permissions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;

import EmpireCoreLib.Configuration.Config;
import EmpireCoreLib.JSON.SerializerTemplate;
import EmpireCoreLib.Localization.LocalizationManager;
import EmpireCoreLib.Proxies.PermissionProxy;
import EmpireCoreLib.Utilities.MinecraftLogicUtilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public class User {

	String 				Username 		= "";
	String 				Prefix 			= "";
	String 				Suffix			= "";
	String				Nickname		= "";
	boolean				Operator		= false;
	boolean				FakePlayer 		= false;
	List<Node> 			Nodes 			= new ArrayList<Node>();
	List<Group> 		Groups 			= new ArrayList<Group>();
	Group				DefaultGroup    = new Group("Default", 0 , "", "");
	UUID 				uuid 			= null;
	
	public final PermissionsContainer permsContainer = new PermissionsContainer();
	public final Meta.Container metaContainer = new Meta.Container();
	
	public User(String Username, String Prefix, String Suffix, String Nickname, boolean Operator, boolean FakePlayer, UUID uuid) {
		setUsername(Username);
		setPrefix(Prefix);
		setSuffix(Suffix);
		setNickName(Nickname);
		setOperator(Operator);
		setFakePlayer(FakePlayer);
		setUUID(uuid);
		
		if (getGroups() == null) {
			this.Groups.add(DefaultGroup);
		}
		
	}
	
	public User(UUID uuid, Group group) {
		this.uuid = uuid;
		if (getGroups().equals(group) == false) {
			this.Groups.add(group);
		}
	}
	
	public User(UUID uuid) {
		this.uuid = uuid;
	}
	public boolean hasPermission(String permission) {
		SimplePermissionLevels permLevel = permsContainer.hasPermission(permission);

		if (permLevel == SimplePermissionLevels.ALLOWED) {
			return true;
		} else if (permLevel == SimplePermissionLevels.DENIED) {
			return false;
		}
		Group group = getGroups();
		return (group != null && group.hasPermission(permission) == SimplePermissionLevels.ALLOWED)
				|| (Config.instance.fullAccessForOPS.get() && isOP(uuid));
	}
	
	public Boolean isOP(UUID uuid) {
		
		EntityPlayer player =  MinecraftLogicUtilities.getMinecraftServer().getPlayerList().getPlayerByUUID(uuid);
		GameProfile profile = MinecraftLogicUtilities.getMinecraftServer().getPlayerList().getOppedPlayers().getGameProfileFromName(player.getName());
		if (profile.getId() == uuid) {
			return true;
		}
		return false;
	}

	public ITextComponent toChatMessage() {
		return LocalizationManager.get("Empires.format.user.short", Username);
	}
	//Set Methods:
	
	public void setUsername(String username) {
		this.Username = username;
	}
	
	public void setPrefix(String prefix) {
		this.Prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.Suffix = suffix;
	}
	
	public void setNickName(String nickname) {
		this.Nickname = nickname;
	}
	
	public void setOperator(Boolean value) {
		this.Operator = value;
	}
	
	public void setFakePlayer(Boolean value) {
		this.Operator = value;
	}
	
	public void setNode(Node node) {
		if (this.Nodes.contains(node) == false) {
			this.Nodes.add(node);
		}
	}
	
	public void setGroup(Group group) {
		if (this.Groups.contains(group) == false) {
			this.Groups.add(group);
		}	
	}
	
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	
	//Get Methods:
	
	public String getUsername() {
		return this.Username;	
	}
	public String getPrefix() {
		return this.Prefix;	
	}
	public String getSuffix() {
		return this.Suffix;	
	}
	public String getNickName() {
		return this.Nickname;	
	}
	public Boolean getOperator() {
		return this.Operator;	
	}
	public Boolean getFakePlayer() {
		return this.FakePlayer;	
	}
	public Node getNodes() {
		for (int i=0; i < this.Nodes.size(); i++) {
			return this.Nodes.get(i);
		}
	return null;		
	}
	
	public Boolean hasNode(Node node) {
		for (int i=0; i < this.Nodes.size(); i++) {
			Node possibleNode = this.Nodes.get(i);
			if (possibleNode.equals(node)) {
				return true;
			}
		}
		return false;
	}

	public Group getGroups() {
		for (int i=0; i < this.Groups.size(); i++) {
			return this.Groups.get(i);
		}
	return null;	
	}
	
	public UUID getUUID() {
		return this.uuid;	
	}
	public static class Serializer extends SerializerTemplate<User> {

		public void register(GsonBuilder builder) {
			builder.registerTypeAdapter(User.class, this);
			new Meta.Container.Serializer().register(builder);
		}

		
		public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
			User user = new User(uuid);
			JsonElement lastPlayerName = jsonObject.get("player");
			if (lastPlayerName != null) {
				user.Username = lastPlayerName.getAsString();
			}
			JsonElement group = jsonObject.get("groups");
			if (group != null) {
				user.setGroup(((EmpireCoreBridge) PermissionProxy.getPermissionManager()).groups
						.get(group.getAsString()));
			}
			if (jsonObject.has("permissions")) {
				user.permsContainer.addAll(ImmutableList
						.copyOf(context.<Node[]>deserialize(jsonObject.get("permissions"), Node[].class)));
			}
			if (jsonObject.has("meta")) {
				user.metaContainer
						.addAll(context.<Meta.Container>deserialize(jsonObject.get("meta"), Meta.Container.class));
			}
			
			 user.setPrefix(jsonObject.get("Prefix").getAsString());
			 user.setPrefix(jsonObject.get("Suffix").getAsString());
			 user.setPrefix(jsonObject.get("Nickname").getAsString());

			return user;
		}

		@Override
		public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();

			json.addProperty("uuid", user.uuid.toString());
			if (user.Username != null) {
				json.addProperty("player", user.Username);
			}
			json.add("Prefix", context.serialize(user.getPrefix()));
			json.add("Suffix", context.serialize(user.getSuffix()));
			json.add("Nickname", context.serialize(user.getNickName()));
			json.addProperty("groups", user.getGroups().getName());
			if (!user.permsContainer.isEmpty()) {
				json.add("permissions", context.serialize(user.permsContainer));
			}
			if (!user.metaContainer.isEmpty()) {
				json.add("meta", context.serialize(user.metaContainer));
			}

			return json;
		}
	}

	public static class Container extends ArrayList<User>  {

		private Group defaultGroup;

		public boolean add(UUID uuid) {
			if (get(uuid) == null) {
				Group group = (defaultGroup == null)
						? ((EmpireCoreBridge) PermissionProxy.getPermissionManager()).groups.get("default")
						: defaultGroup;
				User newUser = new User(uuid, group);
				add(newUser);
				return true;
			}
			return false;
		}

		public User get(UUID uuid) {
			for (User user : this) {
				if (user.uuid.equals(uuid)) {
					return user;
				}
			}
			return null;
		}

		public Group getPlayerGroup(UUID uuid) {

			for (User user : this) {
				if (user.uuid.equals(uuid)) {
					return user.getGroups();
				}
			}

			User user = new User(uuid, defaultGroup);
			add(user);
			return defaultGroup;
		}

		public boolean contains(UUID uuid) {
			for (User user : this) {
				if (user.uuid.equals(uuid)) {
					return true;
				}
			}
			return false;
		}
	}
	
	
}
